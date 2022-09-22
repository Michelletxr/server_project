package br.com.imd.model;
import java.util.StringTokenizer;


public class ObjResponse {
    private int statusCode;
    private String response_body;

    public ObjResponse(int statusCode, String response_body) {
        this.statusCode = statusCode;
        this.response_body = response_body;
    }

    public static String getResponseBody(String response)
    {
        String response_body = "";
        String[] response_lines = response.split("\n");
        int total_lines = response_lines.length;
        for (int i = 0; i < total_lines; i ++)
        {
            String line = response_lines[i];
            if(line.isBlank())
            {
                for (int j = i; j < total_lines; j++)
                {
                    line = response_lines[j];

                    if(line!=null){response_body = response_body + line;}
                }
                break;
            }
        }
         return response_body;
    }

    public static String getUri(String str){
        String uri = "/";

        if(str!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(str);
            String token = tokenizer.nextToken();
            if( !"HTTP/1.0".equals(token))
            {
                 uri = tokenizer.nextToken();
            }
        }

        return uri;
    }

    public static Integer getStatusCode(String str){
        String statusCode = "500";
        System.out.println("Status code" + str);
        if(str!=null)
        {
            StringTokenizer tokenizer = new StringTokenizer(str);
            String token = tokenizer.nextToken();
            if( "HTTP/1.0".equals(token))
            {
                statusCode = tokenizer.nextToken();
            }

        }
        return Integer.parseInt(statusCode);
    }


}
