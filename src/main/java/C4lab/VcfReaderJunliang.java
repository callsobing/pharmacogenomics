package C4lab;

/**
 * Created by NIGHT on 2016/3/24.
 */

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;


import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import java.io.*;


public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = "D:/lab/A0087Y_09182015_bwamem.filtered.haplotype.SnpIndel.vcf";

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if(vctx.getAlternateAlleles().get(0).length()>100)
                    System.out.println(" length: "+vctx.getAlternateAlleles().get(0).length()+" ref: "+vctx.getReference() +
                            " alt:" + vctx.getAlternateAlleles().get(0) + " GT: " +
                            vctx.getGenotype(vctx.getSampleNamesOrderedByName().get(0)).getGenotypeString() );
            }
        }
    }
}

