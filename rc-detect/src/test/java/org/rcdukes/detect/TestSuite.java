package org.rcdukes.detect;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ TestCannyConfig.class,TestLaneDetection.class,TestImageCollector.class,TestImageFetcher.class,TestHoughLinesDetector.class })
/**
 * TestSuite for detect aka daisy
 * 
 * @author wf
 *
 *         no content necessary - annotation has info
 */
public class TestSuite {
}
