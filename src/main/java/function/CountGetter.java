package function;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by yat on 2016/5/5.
 */
public class CountGetter implements Function<VariantContext, List<Integer>> {
    final static Integer ITERATIONS = 10000;
    List<List<Integer>> caseSampleNames;
    List<List<Integer>> controlSampleNames;
    List<Integer> countList = new ArrayList<Integer>();

    public CountGetter(List<List<Integer>> caseSampleNames, List<List<Integer>> controlSampleNames) {
        this.caseSampleNames = caseSampleNames;
        this.controlSampleNames = controlSampleNames;
    }

    @Override
    public List<Integer> call(VariantContext vctx) throws Exception {
        for(Allele allele: vctx.getAlternateAlleles()) {
            ArrayList<Boolean> sampleContainsAllele = new ArrayList<Boolean>();
            for (int k = 0; k < ITERATIONS; k++) {
                if (k == 0) {
                    // Pre-compute
                    sampleContainsAllele.addAll(vctx.getSampleNamesOrderedByName().stream().map(sample -> vctx.getGenotype(sample).countAllele(allele) > 0).collect(Collectors.toList()));
                }
                if (checkAlleleNotPresentInAllControl(controlSampleNames.get(k), sampleContainsAllele) &&
                        checkAllelePresentInAllCase(caseSampleNames.get(k), sampleContainsAllele)) {
                    int kk = countList.get(k);
                    countList.set(k, kk + 1);
                }
            }
        }
        return countList;
    }

    public static boolean checkAlleleNotPresentInAllControl(List<Integer> controlSamples, ArrayList<Boolean> sampleContainsAllele){
        for(Integer sampleIdx: controlSamples){
            if(sampleContainsAllele.get(sampleIdx)){
                return false;
            }
        }
        return true;
    }

    public static boolean checkAllelePresentInAllCase(List<Integer> caseSamples, ArrayList<Boolean> sampleContainsAllele){
        for(Integer sampleIdx: caseSamples){
            if(!sampleContainsAllele.get(sampleIdx)){
                return false;
            }
        }
        return true;
    }
}
