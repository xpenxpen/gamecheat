package org.xpen.capcom.aceattorney;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xpen.capcom.aceattorney.fileformat.Img;
import org.xpen.util.UserSetting;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;

/**
 * Ace Attorney Investigations: Miles Edgeworth
 * 逆转检事1
 * 逆転検事1
 * 
 *
 */
public class GyakutenKenji1Img {
    
    private static final Logger LOG = LoggerFactory.getLogger(GyakutenKenji1Img.class);
    private static final String FILE_SUFFIX_BIN = "bin";
    
    public static void main(String[] args) throws Exception {
        UserSetting.rootInputFolder = "D:/soft/ga/nds/8100412/root/files/myex";
        UserSetting.rootOutputFolder = "D:/soft/ga/nds/8100412/root/files/myex2";
        String fileName = "romfile.bin";
        
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        
        RangeMap<Integer, Integer> rangeMap = buildRangeMap();
        
        int width = 256;
        
        int totalCount = 0;
        int handleCount = 0;
        
        Img img = new Img();
        
        Iterator<Entry<Range<Integer>, Integer>> iterator = rangeMap.asMapOfRanges().entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<Range<Integer>, Integer> entry = iterator.next();
            Range<Integer> key = entry.getKey();
            Integer value = entry.getValue();
            for (int cur = key.lowerEndpoint(), up = key.upperEndpoint(); cur <= up; cur++) {
                totalCount++;
                String fourDigit = StringUtils.leftPad(String.valueOf(cur), 4, '0');
                Path path = Paths.get(UserSetting.rootInputFolder, "romfile/" + fourDigit);
                try {
                    img.handle(path, value, width);
                    handleCount++;
                } catch (Exception e) {
                    LOG.warn("Error occurred, skip {}", cur);
                } finally {
                }
            }
            
        }
        
        stopWatch.stop();
        System.out.println("-----ALL OK, cost time = "+stopWatch.getTime(TimeUnit.SECONDS)+ "s");
        System.out.println("totalCount= "+totalCount + ",handleCount= "+handleCount);
    }

    private static RangeMap<Integer, Integer> buildRangeMap() {
        RangeMap<Integer, Integer> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closed(55, 129), 130);
        rangeMap.put(Range.closed(132, 149), 130);
        rangeMap.put(Range.closed(152, 173), 174);
        rangeMap.put(Range.closed(175, 200), 201);
        rangeMap.put(Range.closed(202, 220), 221);
        rangeMap.put(Range.closed(222, 235), 236);
        rangeMap.put(Range.closed(237, 259), 260);
        rangeMap.put(Range.closed(261, 262), 263);
        rangeMap.put(Range.closed(264, 289), 290);
        rangeMap.put(Range.closed(291, 296), 297);
        rangeMap.put(Range.closed(298, 299), 300);
        rangeMap.put(Range.closed(301, 332), 333);
        rangeMap.put(Range.closed(334, 348), 349);
        rangeMap.put(Range.closed(350, 369), 370);
        rangeMap.put(Range.closed(371, 385), 386);
        rangeMap.put(Range.closed(387, 406), 407);
        rangeMap.put(Range.closed(408, 418), 419);
        rangeMap.put(Range.closed(421, 440), 441);
        rangeMap.put(Range.closed(442, 453), 454);
        rangeMap.put(Range.closed(455, 457), 420);
        rangeMap.put(Range.closed(458, 460), 461);
        rangeMap.put(Range.closed(462, 477), 478);
        rangeMap.put(Range.closed(479, 504), 505);
        rangeMap.put(Range.closed(506, 531), 532);
        rangeMap.put(Range.closed(533, 580), 581);
        rangeMap.put(Range.closed(582, 587), 588);
        rangeMap.put(Range.closed(589, 591), 592);
        rangeMap.put(Range.closed(593, 601), 602);
        rangeMap.put(Range.closed(603, 610), 611);
        rangeMap.put(Range.closed(612, 616), 617);
        rangeMap.put(Range.closed(618, 621), 622);
        rangeMap.put(Range.closed(623, 632), 633);
        rangeMap.put(Range.closed(634, 643), 644);
        rangeMap.put(Range.closed(645, 662), 663);
        rangeMap.put(Range.closed(664, 680), 681);
        rangeMap.put(Range.closed(682, 692), 693);
        rangeMap.put(Range.closed(694, 696), 697);
        rangeMap.put(Range.closed(698, 700), 701);
        rangeMap.put(Range.closed(702, 708), 709);
        rangeMap.put(Range.closed(710, 714), 715);
        rangeMap.put(Range.closed(716, 721), 722);
        rangeMap.put(Range.closed(723, 724), 726);
        rangeMap.put(Range.closed(727, 730), 731);
        rangeMap.put(Range.closed(732, 737), 738);
        rangeMap.put(Range.closed(739, 759), 760);
        rangeMap.put(Range.closed(761, 765), 766);
        rangeMap.put(Range.closed(767, 771), 772);
        rangeMap.put(Range.closed(773, 775), 776);
        rangeMap.put(Range.closed(777, 777), 778);
        rangeMap.put(Range.closed(779, 779), 780);
        rangeMap.put(Range.closed(781, 783), 784);
        rangeMap.put(Range.closed(785, 787), 788);
        rangeMap.put(Range.closed(791, 793), 794);
        rangeMap.put(Range.closed(795, 798), 799);
        rangeMap.put(Range.closed(800, 806), 807);
        rangeMap.put(Range.closed(808, 809), 810);
        rangeMap.put(Range.closed(811, 813), 814);
        rangeMap.put(Range.closed(815, 817), 818);
        rangeMap.put(Range.closed(819, 821), 822);
        rangeMap.put(Range.closed(823, 824), 825);
        rangeMap.put(Range.closed(826, 829), 830);
        return rangeMap;
    }

}
