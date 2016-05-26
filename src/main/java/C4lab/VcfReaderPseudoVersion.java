package C4lab;

import function.CountGetter;
import function.MergeCounts;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReaderUtil;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFCodec;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.*;
import java.util.*;

public class VcfReaderPseudoVersion implements Serializable {
    public static void main(String[] args) throws IOException {
        final String vcfPath = args[0];

        // TODO:你的rsID list
        final List<String> rsIDs = new ArrayList<>();

        // TODO:改成你們各自的名字
        SparkConf conf = new SparkConf().setAppName("allele-freq-calculator");
        JavaSparkContext sc = new JavaSparkContext(conf);

        JavaRDD<String> file = sc.textFile(vcfPath);
        final String VCFHeaderStrings = file
                .filter(line -> line.startsWith("#"))
                .reduce((s1, s2) -> s1 + "\n" + s2);

        JavaRDD<String> lines = file.filter(line -> !line.startsWith("#"));

        JavaRDD<VariantContext> vctx = lines.mapPartitions(
                line -> {
                    final VCFCodec codec = new VCFCodec();
                    codec.readActualHeader(new LineIteratorImpl(LineReaderUtil.fromStringReader(
                            new StringReader(VCFHeaderStrings), LineReaderUtil.LineReaderOption.SYNCHRONOUS)));

                    List<VariantContext> col = new ArrayList<>();
                    line.forEachRemaining(s -> col.add(codec.decode(s)));

                    return col;
                }
        );

        List<Float> output =
                vctx.filter(new Function<VariantContext, Boolean>() {
                    @Override
                    public Boolean call(VariantContext innerVctx) throws Exception {
                        // TODO: 判斷rsID在不在
                        return rsIDs.contains(innerVctx.getID());
                    }
                }).map(new Function<VariantContext, Float>() {
                    @Override
                    public Float call(VariantContext vctx) throws Exception {
                        Float alleleFreq = 0.0f;
                        // TODO:加入你們算AF的邏輯在這裡
                        return alleleFreq;
                    }
                }).collect();

        //TODO:Output結果
    }
}
