package gov.ca.dwr.wrims.tokenreplacer;

import java.util.Map;

public class MapTokenResolver implements ITokenResolver {

	protected Map<String, String> tokenMap;

	public MapTokenResolver(Map<String, String> tokenMap) {
		this.tokenMap = tokenMap;
	}

	public String resolveToken(String tokenName) {
		return this.tokenMap.get(tokenName);
	}

}