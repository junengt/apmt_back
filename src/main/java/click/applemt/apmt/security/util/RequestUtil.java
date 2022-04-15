package click.applemt.apmt.security.util;

import com.google.firebase.ErrorCode;

public class RequestUtil {

    public static String getAuthorizationToken(String header){

        if(header == null || !header.startsWith("Bearer ")){
            throw new IllegalArgumentException();
        }

        String[] parts = header.split(" ");
        if(parts.length != 2){
            throw new IllegalArgumentException();
        }

        return parts[1];

    }

}
