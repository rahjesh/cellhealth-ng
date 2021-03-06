package cellhealth.core.statistics.parser;

import cellhealth.core.statistics.Stats;
import cellhealth.utils.properties.xml.PmiStatsType;
import com.ibm.websphere.pmi.stat.WSBoundedRangeStatistic;
import com.ibm.websphere.pmi.stat.WSStatistic;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Alberto Pascual on 13/08/15.
 */
public class ParserBoundedRangeStatistic<E extends WSBoundedRangeStatistic> extends AbstractParser<E>{

    private String metricName;
    private E parserStatistic;
    private Map<String, Boolean> mapPmiStatsType;
    private String metricSeparator;
    private String node;
    private String prefix;
    private String unity;

    public ParserBoundedRangeStatistic(PmiStatsType pmiStatsType, WSStatistic wsStatistic, String node, String prefix, String metricName) {
        this.metricName = metricName;
        this.metricSeparator = getMetricSeparator(pmiStatsType);
        this.parserStatistic =  (E) wsStatistic;
        this.unity = this.getUnity(pmiStatsType, this.parserStatistic);
        this.mapPmiStatsType = pmiStatsType.getBoundedRangeStatistic();
        this.prefix = prefix;
        this.node = node;
    }

    public List<Stats> getStatistic() {
        List<Stats> result = new LinkedList<Stats>();
        for (Map.Entry<String,Boolean> entry : this.mapPmiStatsType.entrySet()) {
            if(entry.getValue() != null && entry.getValue()){
                String method = entry.getKey();
                Stats stats = new Stats();
                stats.setHost(this.node);
                String metric = "";
                if("upperBound".equals(method)){
                    metric = String.valueOf(this.parserStatistic.getUpperBound());
                } else if("lowebBound".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getLowerBound());
                } else if("highWaterMark".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getHighWaterMark());
                } else if("lowWaterMark".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getLowWaterMark());
                } else if("current".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getCurrent());
                } else if("integral".equals(method)) {
                    metric = String.valueOf(this.parserStatistic.getIntegral());
                }
                stats.setMetric(this.prefix + "." + this.metricName + this.metricSeparator + method + this.unity + metric + " " + System.currentTimeMillis() / 1000L);
                result.add(stats);
            }
        }
        return result;
    }
}
