package com.aluxian.drizzle.utils;

public class Utils {

    /**
     * Parses the value of a Link header in order to extract the url with rel="next".
     *
     * @param linkHeader The value of the Link header to parse.
     * @return The url with rel="next" or null if not found.
     */
    public static String extractNextUrl(String linkHeader) {
        if (linkHeader != null) {
            String[] links = linkHeader.split(",");

            for (String link : links) {
                String[] segments = link.split(";");
                if (segments.length < 2) {
                    continue;
                }

                String[] relPart = segments[1].trim().split("=");
                if (relPart.length < 2) {
                    continue;
                }

                String relValue = relPart[1];
                if (relValue.startsWith("\"") && relValue.endsWith("\"")) {
                    relValue = relValue.substring(1, relValue.length() - 1);
                }

                if ("next".equals(relValue)) {
                    String linkPart = segments[0].trim();

                    if (linkPart.startsWith("<") && linkPart.endsWith(">")) {
                        return linkPart.substring(1, linkPart.length() - 1);
                    }
                }
            }
        }

        return null;
    }

}
