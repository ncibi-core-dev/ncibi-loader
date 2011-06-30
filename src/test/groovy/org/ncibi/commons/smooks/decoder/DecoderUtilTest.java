package org.ncibi.commons.smooks.decoder;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

public class DecoderUtilTest
{
    @Test
    public void testTypeValueFromWithDefault()
    {
        final String entry = "pmid:1234";
        final TypeValue tvalue = DecoderUtil.typeValueFrom(entry, null);
        Assert.assertTrue(tvalue.getType().equals("pmid"));
        Assert.assertTrue(tvalue.getValue().equals("1234"));
    }

    @Test
    public void testTypeValueFromWithRegex()
    {
        final String entry = "pmid:/1234";
        final TypeValue tvalue = DecoderUtil.typeValueFrom(entry, "\\r[:/]");
        Assert.assertTrue(tvalue.getType().equals("pmid"));
        Assert.assertTrue(tvalue.getValue().equals("1234"));
    }

    @Test
    public void testTypeValueListWithRegexBoth()
    {
        final String line = "pmid:/1234,pmid:4567,,pmid:7890,;pmid:/5678,";
        final ImmutableList<TypeValue> expectedValues = ImmutableList.of(new TypeValue("pmid",
                "1234"), new TypeValue("pmid", "4567"), new TypeValue("pmid", "7890"),
                new TypeValue("pmid", "5678"));
        TypeValue defaultValue = new TypeValue("pmid", "default");
        final List<TypeValue> items = DecoderUtil.typeValueListFrom(line, defaultValue, "\\r[,;]",
                "\\r[:/]");

        Assert.assertTrue(items.containsAll(expectedValues));
    }
}
