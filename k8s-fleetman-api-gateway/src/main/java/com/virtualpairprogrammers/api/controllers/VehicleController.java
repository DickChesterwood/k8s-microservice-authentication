package com.virtualpairprogrammers.api.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPublicKey;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.KeyLengthException;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jose.proc.DefaultJOSEObjectTypeVerifier;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTClaimsSetVerifier;
import com.virtualpairprogrammers.api.domain.LatLong;
import com.virtualpairprogrammers.api.domain.VehiclePosition;
import com.virtualpairprogrammers.api.services.PositionTrackingExternalService;

@Controller
@RequestMapping("/")
public class VehicleController 
{	
	@Autowired
	private SimpMessageSendingOperations messagingTemplate;

	// TODO decide what to do with this
	@Autowired
	private PositionTrackingExternalService externalService;

	private Date lastUpdateTime = new java.util.Date();

	@GetMapping("/")
	@ResponseBody
	/**
	 * This is just a test mapping so we can easily check the API gateway is standing.
	 * When running through the Angular Front end, can visit this URL at /api/
	 */
	public String apiTestUrl()
	{
		return "<p>Fleetman API Gateway at " + new Date() + "</p>";
	}

	@GetMapping("/history/{vehicleName}")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public Collection<LatLong> getHistoryFor(@PathVariable("vehicleName") String vehicleName)
	{
		Collection<LatLong> results = new ArrayList<>();
		Collection<VehiclePosition> vehicles = externalService.getHistoryFor(vehicleName);
		for (VehiclePosition next: vehicles)
		{
			LatLong position = new LatLong(next.getLat(), next.getLongitude()); 
			results.add(position);
		}
		Collections.reverse((List<?>) results);
		return results;
	}

	// TODO restore this
//	@Scheduled(fixedRate=2000)
	public void updatePositions()
	{
		Collection<VehiclePosition> results = externalService.getAllUpdatedPositionsSince(lastUpdateTime);
		this.lastUpdateTime = new Date();
		for (VehiclePosition next: results)
		{
			this.messagingTemplate.convertAndSend("/vehiclepositions/messages", next);
		}
	}
	
	/**
	 * This is our testing method for authorization. 
	 * 
	 * Only holders of valid JWT tokens with an "admin" claim are allowed!
	 * 
	 * Done manually for illustration, we will next move to doing this using Spring Security
	 * @throws JOSEException 
	 * @throws ParseException 
	 * @throws BadJOSEException 
	 * @throws MalformedURLException 
	 */
	@GetMapping("/vehicles")
	@ResponseBody
	@CrossOrigin(origins = "*")
	public ResponseEntity<List<String>> getAllVehicles(@RequestHeader (name="authorization") String token) throws ParseException, BadJOSEException, MalformedURLException, JOSEException
	{
		token =token.split("Bearer ")[1];
		System.out.println("Bearer token is " + token);
		
		// from https://connect2id.com/products/nimbus-jose-jwt/examples/validating-jwt-access-tokens
		ConfigurableJWTProcessor<SecurityContext> jwtProcessor =
		    new DefaultJWTProcessor<>();
		jwtProcessor.setJWSTypeVerifier(
			    new DefaultJOSEObjectTypeVerifier<>(new JOSEObjectType("jwt")));
		
		// Our public keyset
		JWKSource<SecurityContext> keySource =
			    new RemoteJWKSet<>(new URL("https://dev-ojpq3kdh.eu.auth0.com/.well-known/jwks.json"));

		JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;
		JWSKeySelector<SecurityContext> keySelector =
			    new JWSVerificationKeySelector<>(expectedJWSAlg, keySource);
		
		jwtProcessor.setJWSKeySelector(keySelector);
		jwtProcessor.setJWTClaimsSetVerifier((JWTClaimsSetVerifier<SecurityContext>) new DefaultJWTClaimsVerifier(
			    new JWTClaimsSet.Builder().issuer("https://dev-ojpq3kdh.eu.auth0.com/").build(),
			    new HashSet<>(Arrays.asList("sub", "iat", "exp","permissions")))); 
		
		JWTClaimsSet claimsSet;
		try {
			claimsSet = jwtProcessor.process(token,null); // null = context, not needed apparently!
		} 
		catch (BadJWSException | java.text.ParseException e) {
			// failed signature - I think this should be http 401 but not 100% sure. We don't know who the client is and they haven't supplied valid authentication.
			return new ResponseEntity<List<String>>(HttpStatus.UNAUTHORIZED);
		} 
	    
		System.out.println("Well, we did it, here's some output: " + claimsSet.toJSONObject());
		
//		Map<String, Object> claims = claimsSet.toJSONObject();
//		JSONArray permissions = (JSONArray) claims.get("permissions");
//		if (permissions.size() < 1)
//		{
//			return new ResponseEntity<List<String>>(HttpStatus.FORBIDDEN);
//		}
//		String permission = permissions.get(0).toString();
//		System.out.println("permissions were " + permission); // let's assume each user has one permission only
		
		// Now we need to see if they are in a valid role. Only admin will be allowed here. Need to set up an additional claim...
		// TODO this is pickup point - we don't have enough auth0 knowledge yet - looks like we need the guide at:
//		https://auth0.com/docs/quickstart/spa/angular/02-calling-an-api						
		
		List<String> results = new ArrayList<>();
		results.add("Toyota Hiace");
		results.add("Ford Transit");
		return new ResponseEntity<List<String>>(results,HttpStatus.OK);
	}
	
}
