package luke.dennis.bletchleypark;

import java.util.*;

/**
 * This is a brute force approach, with some logic to greatly reduce the number of solutions that are checked, to solving
 * a problem found whilst on a trip to Bletchley Park.
 * <p>
 * The problem is as follows:
 * <p>
 * Solve the following square where the values p0, p1, ..., p15 are the numbers 1, 2, ..., 16 in some order. Each row and
 * column must satisfy the equation described and multiplication and division are performed before addition and subtraction.
 * <p>
 * p0  + p1  + p2  * p3  = 90
 * -     *     +     +
 * p4  + p5  + p6  + p7  = 33
 * +     -     *     +
 * p8  + p9  * p10 - p11 = 8
 * +     +     /     -
 * p12 * p13 + p14 + p15 = 106
 * =     =     =     =
 * 11    26    76    12
 * <p>
 * This problem was found on what looked like a children's worksheet. I would be interested to hear a non brute force way
 * of solving it if any exists.
 */
public class Main {

    private static final List<Integer> availableNumbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16);
    private static List<Integer> remainingNumbers;

    private static final int MIN_AVAILABLE_VALUE = 1;
    private static final int MAX_AVAILABLE_VALUE = availableNumbers.size();
    private static List<Integer> possibleSolution = new ArrayList<Integer>(availableNumbers.size());

    private static List<List<Integer>> solutions = new ArrayList<List<Integer>>();

    private static int lastPosition12;

    /**
     * Use knowledge about pairs of numbers that can be determined from looking at the equations and run through the
     * possible solutions based on these.
     * <p>
     * This code is pretty ugly but effective in reducing the number of permutations that have to be checked to a manageable
     * number.
     */
    public static void main(String[] args) {
        for (Integer p2 = MIN_AVAILABLE_VALUE; p2 <= MAX_AVAILABLE_VALUE; p2++) {
            for (Integer p3 = MIN_AVAILABLE_VALUE; p3 <= MAX_AVAILABLE_VALUE; p3++) {
                if (isInvalidPosition2Position3Combination(p2, p3)) {
                    continue;
                }
                for (Integer p12 = MIN_AVAILABLE_VALUE; p12 <= MAX_AVAILABLE_VALUE; p12++) {
                    for (Integer p13 = MIN_AVAILABLE_VALUE; p13 <= MAX_AVAILABLE_VALUE; p13++) {
                        List<Integer> pair0 = Arrays.asList(p2, p3);
                        List<Integer> pair1 = Arrays.asList(p12, p13);
                        if (clashBetweenPairs(pair0, pair1)
                                || isInvalidPosition12Position13Combination(p12, p13)) {
                            continue;
                        }
                        for (Integer p1 = MIN_AVAILABLE_VALUE; p1 <= MAX_AVAILABLE_VALUE; p1++) {
                            for (Integer p5 = MIN_AVAILABLE_VALUE; p5 <= MAX_AVAILABLE_VALUE; p5++) {
                                List<Integer> pair2 = Arrays.asList(p1, p5);
                                if (clashBetweenPairs(pair0, pair2)
                                        || clashBetweenPairs(pair1, pair2)
                                        || isInvalidPosition1Position5Combination(p1, p5)) {
                                    continue;
                                }
                                for (Integer p9 = MIN_AVAILABLE_VALUE; p9 <= MAX_AVAILABLE_VALUE; p9++) {
                                    for (Integer p10 = MIN_AVAILABLE_VALUE; p10 <= MAX_AVAILABLE_VALUE; p10++) {
                                        List<Integer> pair3 = Arrays.asList(p9, p10);
                                        if (clashBetweenPairs(pair0, pair3)
                                                || clashBetweenPairs(pair1, pair3)
                                                || clashBetweenPairs(pair2, pair3)
                                                || isInvalidPosition9Position10Combination(p9, p10)) {
                                            continue;
                                        }
                                        for (Integer p6 = MIN_AVAILABLE_VALUE; p6 <= MAX_AVAILABLE_VALUE; p6++) {
                                            for (Integer p14 = MIN_AVAILABLE_VALUE; p14 <= MAX_AVAILABLE_VALUE; p14++) {
                                                List<Integer> pair4 = Arrays.asList(p6, p14);
                                                if (clashBetweenPairs(pair0, pair4)
                                                        || clashBetweenPairs(pair1, pair4)
                                                        || clashBetweenPairs(pair2, pair4)
                                                        || clashBetweenPairs(pair3, pair4)
                                                        || isInvalidPosition6Position10Position14Combination(p6, p10, p14)) {
                                                    continue;
                                                }
                                                updateRemainingNumbers(p2, p3, p12, p13, p1,
                                                        p5, p9, p10, p6, p14);
                                                checkAllPermutations(p2, p3, p12, p13, p1,
                                                        p5, p9, p10, p6, p14, remainingNumbers, 0);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        printFoundSolutions();
    }

    /**
     * Check all permutations possible with the given values. Print updates to provide an estimate of how long the code
     * will take to complete the task and record any solutions that solve all the equations.
     */
    private static void checkAllPermutations(int p2, int p3, int p12, int p13, int p1, int p5, int p9, int p10,
                                             int p6, int p14, List<Integer> remainingNumbers, int k) {
        for (int i = k; i < remainingNumbers.size(); i++) {
            Collections.swap(remainingNumbers, i, k);
            checkAllPermutations(p2, p3, p12, p13, p1, p5, p9, p10, p6, p14, remainingNumbers, k + 1);
            Collections.swap(remainingNumbers, k, i);
        }
        if (k == remainingNumbers.size()) {
            convertToPossibleSolution(p2, p3, p12, p13, p1, p5, p9,
                    p10, p6, p14, remainingNumbers);
            if (isSolution()) {
                solutions.add(new ArrayList<Integer>(possibleSolution));
            }
            printUpdate(p2, p3, p12);
        }
    }

    /**
     * Substitute the already determined values into the list filling in the other number with the permutation.
     */
    private static void convertToPossibleSolution(int p2, int p3, int p12, int p13, int p1, int p5,
                                                  int p9, int p10, int p6, int p14, List<Integer> remainingNumbers) {
        possibleSolution.clear();
        possibleSolution = new ArrayList<Integer>(remainingNumbers);
        possibleSolution.add(1, p1);
        possibleSolution.add(2, p2);
        possibleSolution.add(3, p3);
        possibleSolution.add(5, p5);
        possibleSolution.add(6, p6);
        possibleSolution.add(9, p9);
        possibleSolution.add(10, p10);
        possibleSolution.add(12, p12);
        possibleSolution.add(13, p13);
        possibleSolution.add(14, p14);
    }

    /**
     * Print each time the number that changes third least changes. This means that each line printed will represent roughly
     * 1/1000th of the possible permutations.
     */
    private static void printUpdate(int p2, int p3, int p12) {
        if (lastPosition12 != possibleSolution.get(12)) {
            lastPosition12 = possibleSolution.get(12);

            System.out.println("At " + new Date() + " the last checked combination was " +
                    Arrays.asList(p2, p3, p12));
            if (!solutions.isEmpty()) {
                printFoundSolutions();
            }
        }
    }

    /**
     * Remove the numbers already assigned to a position leaving a list to be permuted over.
     */
    private static void updateRemainingNumbers(Integer p2, Integer p3, Integer p12, Integer p13, Integer p1,
                                               Integer p5, Integer p9, Integer p10, Integer p6, Integer p14) {
        remainingNumbers = new ArrayList<Integer>(availableNumbers);
        remainingNumbers.remove(p2);
        remainingNumbers.remove(p3);
        remainingNumbers.remove(p12);
        remainingNumbers.remove(p13);
        remainingNumbers.remove(p1);
        remainingNumbers.remove(p5);
        remainingNumbers.remove(p9);
        remainingNumbers.remove(p10);
        remainingNumbers.remove(p6);
        remainingNumbers.remove(p14);
    }

    /**
     * Looking at the fourth row we have:
     * <p>
     * p12 * p13 + p14 + p15 = 106
     * <p>
     * Now p14 + p15 is at least 3 and at most 31 meaning that p12 * p13 must be in the range 75-103.
     */
    private static boolean isInvalidPosition12Position13Combination(Integer p12, Integer p13) {
        return p12.equals(p13) || p12 * p13 > 103 || p12 * p13 < 75;
    }

    /**
     * This method assumes that each pair contains two unequal numbers and then determines if there are any common
     * numbers between the two pairs.
     *
     * @return true if there are any common numbers in the two pairs; false otherwise
     */
    private static boolean clashBetweenPairs(List<Integer> pair1, final List<Integer> pair2) {
        for (Integer pair1Number : pair1) {
            if (pair2.contains(pair1Number)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Looking at the second column we have:
     * <p>
     * p1 * p5 - p9 + p11 = 26
     * <p>
     * Now p11 - p9 is at least -15 and at most 15 meaning that p1 * p5 must be in the range 11-41.
     */
    private static boolean isInvalidPosition1Position5Combination(Integer p1, Integer p5) {
        int p1p5Product = p1 * p5;
        return p1.equals(p5) || p1p5Product > 41 || p1p5Product < 11;
    }

    /**
     * Looking at the third column we have:
     * <p>
     * p2 + p6 * p10 / p14 = 76
     * <p>
     * Now p2 is at least 1 and at most 16 meaning that p6 * p10 / p14 must be in the range 60-75.
     */
    private static boolean isInvalidPosition6Position10Position14Combination(Integer p6, Integer p10, Integer p14) {
        int result = p6 * p10 / p14;
        return p6.equals(p14) || result > 75 || result < 60;
    }

    /**
     * Looking at the third row we have:
     * <p>
     * p8 + p9 * p10 - p11 = 8
     * <p>
     * Now p11 - p8 at most 15 meaning that p12 * p13 must be at most 23.
     */
    private static boolean isInvalidPosition9Position10Combination(Integer p9, Integer p10) {
        return p9.equals(p10) || p9 * p10 > 23;
    }

    /**
     * Looking at the first row we have:
     * <p>
     * p0 + p1 + p2 * p3  = 90
     * <p>
     * Now p0 + p1 is at least 3 and at most 31 meaning that p2 * p3 must be in the range 59-87.
     */
    private static boolean isInvalidPosition2Position3Combination(Integer p2, Integer p3) {
        double p2p3Product = p2 * p3;
        return p2.equals(p3) || p2p3Product > 87 || p2p3Product < 59;
    }

    /**
     * Print out all valid solutions
     */
    private static void printFoundSolutions() {
        System.out.println("The found solutions are:");
        for (List<Integer> solution : solutions) {
            System.out.println(solution);
        }
    }

    /**
     * @return true if the solution satifaies all the equations (solutions are assumed to have the numbers 1-16), otherwise false
     */
    private static boolean isSolution() {
        return satisfiesFirstRow(possibleSolution)
                && satisfiesSecondRow(possibleSolution)
                && satisfiesThirdRow(possibleSolution)
                && satisfiesFourthRow(possibleSolution)
                && satisfiesFirstColumn(possibleSolution)
                && satisfiesSecondColumn(possibleSolution)
                && satisfiesThirdColumn(possibleSolution)
                && satisfiesFourthColumn(possibleSolution);
    }

    /**
     * @return true if the solution under test satisfies the equation in the first row, false otherwise
     */
    private static boolean satisfiesFirstRow(List<Integer> possibleSolution) {
        return possibleSolution.get(0) + possibleSolution.get(1) + possibleSolution.get(2) * possibleSolution.get(3) == 90;
    }

    /**
     * @return true if the solution under test satisfies the equation in the second row, false otherwise
     */
    private static boolean satisfiesSecondRow(List<Integer> possibleSolution) {
        return possibleSolution.get(4) + possibleSolution.get(5) + possibleSolution.get(6) + possibleSolution.get(7) == 33;
    }

    /**
     * @return true if the solution under test satisfies the equation in the third row, false otherwise
     */
    private static boolean satisfiesThirdRow(List<Integer> possibleSolution) {
        return possibleSolution.get(8) + possibleSolution.get(9) * possibleSolution.get(10) - possibleSolution.get(11) == 8;
    }

    /**
     * @return true if the solution under test satisfies the equation in the fourth row, false otherwise
     */
    private static boolean satisfiesFourthRow(List<Integer> possibleSolution) {
        return possibleSolution.get(12) * possibleSolution.get(13) + possibleSolution.get(14) + possibleSolution.get(15) == 106;
    }

    /**
     * @return true if the solution under test satisfies the equation in the first column, false otherwise
     */
    private static boolean satisfiesFirstColumn(List<Integer> possibleSolution) {
        return possibleSolution.get(0) - possibleSolution.get(4) + possibleSolution.get(8) + possibleSolution.get(12) == 11;
    }

    /**
     * @return true if the solution under test satisfies the equation in the second column, false otherwise
     */
    private static boolean satisfiesSecondColumn(List<Integer> possibleSolution) {
        return possibleSolution.get(1) * possibleSolution.get(5) - possibleSolution.get(9) + possibleSolution.get(13) == 26;
    }

    /**
     * @return true if the solution under test satisfies the equation in the third column, false otherwise
     */
    private static boolean satisfiesThirdColumn(List<Integer> possibleSolution) {
        return possibleSolution.get(2) + possibleSolution.get(6) * possibleSolution.get(10) / possibleSolution.get(14) == 76;
    }

    /**
     * @return true if the solution under test satisfies the equation in the fourth column, false otherwise
     */
    private static boolean satisfiesFourthColumn(List<Integer> possibleSolution) {
        return possibleSolution.get(3) + possibleSolution.get(7) + possibleSolution.get(11) - possibleSolution.get(15) == 12;
    }

}

