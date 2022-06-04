/*
 * Copyright (c) 2019 - 2021. FlashMonkey Inc. (https://www.flashmonkey.xyz) All rights reserved.
 *
 * License: This is for internal use only by those who are current employees of FlashMonkey Inc, or have an official
 *  authorized relationship with FlashMonkey Inc..
 *
 * DISCLAIMER OF WARRANTY.
 *
 * COVERED CODE IS PROVIDED UNDER THIS LICENSE ON AN "AS IS" BASIS, WITHOUT WARRANTY OF ANY
 *  KIND, EITHER EXPRESS OR IMPLIED, INCLUDING, WITHOUT LIMITATION, WARRANTIES THAT THE COVERED
 *  CODE IS FREE OF DEFECTS, MERCHANTABLE, FIT FOR A PARTICULAR PURPOSE OR NON-INFRINGING. THE
 *  ENTIRE RISK AS TO THE QUALITY AND PERFORMANCE OF THE COVERED CODE IS WITH YOU. SHOULD ANY
 *  COVERED CODE PROVE DEFECTIVE IN ANY RESPECT, YOU (NOT THE INITIAL DEVELOPER OR ANY OTHER
 *  CONTRIBUTOR) ASSUME THE COST OF ANY NECESSARY SERVICING, REPAIR OR CORRECTION. THIS
 *  DISCLAIMER OF WARRANTY CONSTITUTES AN ESSENTIAL PART OF THIS LICENSE.  NO USE OF ANY COVERED
 *  CODE IS AUTHORIZED HEREUNDER EXCEPT UNDER THIS DISCLAIMER.
 *
 */

package type.testtypes;

import ch.qos.logback.classic.Level;
import org.slf4j.LoggerFactory;
import java.util.HashMap;


/**
 * An ordered array of TestCards. The array index is used to get the
 * card from the array. A testType's testBSet contains the index for
 * its type. That index is used during the read session
 * and matches this array. See notes for index relationships.
 * To add a Test class
 * add it's index into the getBitSet bitset array.
 * //@TODO convert testTypes to singletons. and Lazily instantiate them.
 */
public final class TestList {

    private static final ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(TestList.class);



    // The order is important. Cards should be added to
    // the bottom of the list unless testtypes are modified.
    // The index set in the bitSet of each test correlates
    // with this array.
    public static final GenericTestType[] TEST_TYPES = {
            //new AIMode(),
            MultiChoice.getInstance(),
            //new MultiAnswer(),
            QandA.QandATest.getInstance(),
            TrueOrFalse.getInstance(),
            //new FillnTheBlank(),
            //new TurninVideo(),
            //new TurninAudio(),
            //new WriteIn(),
            //new TurnInDraw(),
            //new DrawOnImage(),
            MathCard.getInstance(),
            //GraphCard.getInstance(),
            NoteTaker.getInstance(),// end
    };


    private static final HashMap<Integer, Integer> map = createTable();

    /**
     * Do not instantiate this class. Private
     * constructor.
     */
    private TestList() { /* private constructor */ }


    private static HashMap createTable() {
        HashMap<Integer, Integer> m = new HashMap<>(TEST_TYPES.length);
        for(int i = 0; i < TEST_TYPES.length; i++) {
            m.put(TEST_TYPES[i].getTestType(), i);

            //System.out.println( i + " == TEST_TYPES[i}: " + TEST_TYPES[i].getTestType());

        }
        return m;
    }

    /**
     * Returns the TestType. Each Card can be a different TestType. A card knows its type by
     * it's byteArray or what would be seen as a number. While we only have a few TestTypes,
     * each test type is a single bit flip, IE 2,4,8, or 16 etc... . For forward and backwards
     * compatibility, we store the integer in a HashMep and associate it with the TestType. The
     * HashMap is created when this class is called. Currently it is called by CreateFlash and
     * ReadFlash.
     * @param n = testType
     * @return
     */
    public static GenericTestType selectTest(Integer n)
    {
        if(map == null) {
            createTable();
        }
        LOGGER.setLevel(Level.DEBUG);
        if(n == 31) {
            return TEST_TYPES[0];
        }

        return TEST_TYPES[map.get(n)];
    }
}
