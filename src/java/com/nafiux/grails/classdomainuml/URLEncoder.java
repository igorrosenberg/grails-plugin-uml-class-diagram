package com.nafiux.grails.classdomainuml;

/**
 *
 * @author nafiux
 */
public interface URLEncoder {

        String encode(byte data[]);

        byte[] decode(String s);

}