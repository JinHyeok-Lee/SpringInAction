package com.springinaction.beanwireing;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={CDPlayerConfig.class})
// xml 설정
// (locations= {"/com/springinaction/beanwireing/CDPlayerConfig.xml"})
public class CDPlayerTest {

	@Autowired
	private CompactDisc cd;

	@Test public void test() {
		assertNotNull(cd);
	}

}
