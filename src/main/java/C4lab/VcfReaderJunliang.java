package C4lab;

import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;

import javax.lang.model.type.NullType;
import java.io.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

public class VcfReaderJunliang
{
    public static void main( String[] args ) throws IOException {
        long start = System.currentTimeMillis( );
        VCFCodec vcfCodec = new VCFCodec();
        final String vcfPath = args[0];

        BufferedReader schemaReader = new BufferedReader(new FileReader(vcfPath));

        String line;
        String headerLine = "";
        VariantContext vctx;
        int Count=0;
        boolean AlreadyGetID=false;
        String[] cases =new String[5];
        String[] control =new String[5];

        while ((line = schemaReader.readLine()) != null) {
            if(line.startsWith("#")) {
                headerLine = headerLine.concat(line).concat("\n");
                continue;
            }
            vcfCodec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                    new StringReader(headerLine), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

            if(!line.startsWith("#")) {
                vctx = vcfCodec.decode(line);
                if(!AlreadyGetID) {
                    for (int i = 0; i < 10; i++) {
                        if (i < 5)
                            cases[i] = vctx.getSampleNamesOrderedByName().get(i);
                        else
                            control[i - 5] = vctx.getSampleNamesOrderedByName().get(i);
                    }
                    AlreadyGetID=true;
                }

                boolean isAnswer = true;
                Allele CompareAllele = vctx.getAlternateAlleles().get(0);
                for (int j = 0; j < 5 && isAnswer; j++)
                    isAnswer=(!(vctx.getGenotype(cases[j]).countAllele(CompareAllele)==0))&&(vctx.getGenotype(control[j]).countAllele(CompareAllele)==0);
                if(isAnswer) {
                    Count=Count+1;
                    System.out.println(vctx.getID()+"\t"+Count);
                }
            }
        }
        long end = System.currentTimeMillis( );
        System.out.println("time cost : " + (end - start) +"ms");
    }
}

