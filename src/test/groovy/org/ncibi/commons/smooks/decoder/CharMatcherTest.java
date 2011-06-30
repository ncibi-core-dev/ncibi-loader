package org.ncibi.commons.smooks.decoder;

import java.util.regex.Pattern;

import org.junit.Test;
import org.ncibi.commons.lang.IterableUtil;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;


public class CharMatcherTest
{
    @Test
    public void testCharMatcher()
    {
        String ecnum = CharMatcher.anyOf("123456789.-").retainFrom("1.2.3.-falfjsd");
        System.out.println("ecnum = '" + ecnum + "'");
    }
    
    @Test
    public void testMultipleSplits()
    {
        System.out.println("\n");
        final String enzymes = ";6.2.1.-/5.1.99.4/1.3.3.6/4.2.1.17/1.1.1.35/2.3.1.16(2.3.1.154)/3.1.2.2;";
        final String enzymesCleaned = CharMatcher.anyOf("()").replaceFrom(enzymes, ';');
        System.out.println("enzymesCleaned = '" + enzymesCleaned + "'");
        final Iterable<String> results = Splitter.on(Pattern.compile("[;/]")).omitEmptyStrings().split(enzymesCleaned);
        for (String result : results)
        {
            System.out.println("result = '" + result + "'");
        }
    }
    
    @Test
    public void testCleanPubmed()
    {
        System.out.println("\n");
        final String pubmedids = "1883349*E; 1902669*P";
        for (String dirtyPmid : Splitter.on(';').trimResults().omitEmptyStrings().split(pubmedids))
        {
            String pmid = CharMatcher.JAVA_DIGIT.retainFrom(dirtyPmid);
            System.out.println("pmid = '" + pmid + "'");
        }
    }
    
    @Test
    public void testSplitterWithRegex()
    {
        System.out.println("\n");
        final String line = "pmid:/1234";
        Pattern p = Pattern.compile("[:/]");
        Iterable<String> i = Splitter.on(p).omitEmptyStrings().trimResults().split(line);
        System.out.println(IterableUtil.getAt(i, 0, null));
        System.out.println(IterableUtil.getAt(i, 1, null));
        for (String entry : Splitter.on(p).omitEmptyStrings().trimResults().split(line))
        {
            System.out.println("entry = '" + entry + "'");
        }
    }
}
