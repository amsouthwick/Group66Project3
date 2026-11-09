// -------------------------------------------------------------------------
/**
 *  Write a one-sentence summary of your class here.
 *  Follow it with additional details about its purpose, what abstraction
 *  it represents, and how to use it.
 * 
 *  @author micha
 *  @version Nov 9, 2026
 */
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Evaluates Texas Hold'em hands using each player's two cards plus the board. */
public class HandEvaluator {
    private static final int HIGH_CARD = 1;
    private static final int ONE_PAIR = 2;
    private static final int TWO_PAIR = 3;
    private static final int THREE_OF_A_KIND = 4;
    private static final int STRAIGHT = 5;
    private static final int FLUSH = 6;
    private static final int FULL_HOUSE = 7;
    private static final int FOUR_OF_A_KIND = 8;
    private static final int STRAIGHT_FLUSH = 9;

    /**
     * Returns active players from best hand to worst hand. Players with tied
     * hands appear next to one another in the returned list.
     *
     * @throws IllegalStateException if players is empty
     * @throws IllegalArgumentException if board does not contain exactly five cards
     */
    public List<Player> rankHands(List<Player> players, List<Card> board) {
        if (players == null || players.isEmpty()) {
            throw new IllegalStateException("There must be at least one player.");
        }
        if (board == null || board.size() != 5) {
            throw new IllegalArgumentException("The board must contain exactly five cards.");
        }

        List<PlayerScore> scores = new ArrayList<>();
        for (Player player : players) {
            if (player == null || player.hasFolded()) {
                continue;
            }

            List<Card> cards = new ArrayList<>(board);
            cards.addAll(player.getHand());
            if (cards.size() != 7) {
                throw new IllegalArgumentException("Each player must have exactly two cards.");
            }
            scores.add(new PlayerScore(player, evaluateSevenCards(cards)));
        }

        Collections.sort(scores, Comparator.comparing(PlayerScore::score).reversed());

        List<Player> result = new ArrayList<>();
        for (PlayerScore score : scores) {
            result.add(score.player());
        }
        return result;
    }

    private Score evaluateSevenCards(List<Card> cards) {
        Score best = null;
        for (int a = 0; a < cards.size() - 4; a++) {
            for (int b = a + 1; b < cards.size() - 3; b++) {
                for (int c = b + 1; c < cards.size() - 2; c++) {
                    for (int d = c + 1; d < cards.size() - 1; d++) {
                        for (int e = d + 1; e < cards.size(); e++) {
                            Score current = evaluateFiveCards(List.of(
                                    cards.get(a), cards.get(b), cards.get(c),
                                    cards.get(d), cards.get(e)));
                            if (best == null || current.compareTo(best) > 0) {
                                best = current;
                            }
                        }
                    }
                }
            }
        }
        return best;
    }

    private Score evaluateFiveCards(List<Card> cards) {
        Map<Integer, Integer> counts = new HashMap<>();
        Map<Card.Suit, Integer> suits = new HashMap<>();
        List<Integer> ranks = new ArrayList<>();

        for (Card card : cards) {
            int rank = card.getRank();
            counts.put(rank, counts.getOrDefault(rank, 0) + 1);
            suits.put(card.getSuit(), suits.getOrDefault(card.getSuit(), 0) + 1);
            ranks.add(rank);
        }

        ranks.sort(Collections.reverseOrder());
        boolean flush = suits.containsValue(5);
        int straightHigh = straightHigh(ranks);

        if (flush && straightHigh > 0) {
            return new Score(STRAIGHT_FLUSH, straightHigh);
        }

        int four = rankWithCount(counts, 4);
        if (four > 0) {
            return new Score(FOUR_OF_A_KIND, four, rankWithCount(counts, 1));
        }

        int three = rankWithCount(counts, 3);
        int pair = highestPair(counts);
        if (three > 0 && pair > 0) {
            return new Score(FULL_HOUSE, three, pair);
        }

        if (flush) {
            return new Score(FLUSH, ranks);
        }

        if (straightHigh > 0) {
            return new Score(STRAIGHT, straightHigh);
        }

        if (three > 0) {
            return new Score(THREE_OF_A_KIND, three, highestRanksExcluding(counts, 3, three));
        }

        List<Integer> pairs = ranksWithCount(counts, 2);
        if (pairs.size() >= 2) {
            return new Score(TWO_PAIR, pairs.get(0), pairs.get(1), rankWithCount(counts, 1));
        }

        if (pairs.size() == 1) {
            return new Score(ONE_PAIR, pairs.get(0), highestRanksExcluding(counts, 2, pairs.get(0)));
        }

        return new Score(HIGH_CARD, ranks);
    }

    private int straightHigh(List<Integer> ranks) {
        List<Integer> unique = new ArrayList<>();
        for (int rank : ranks) {
            if (!unique.contains(rank)) {
                unique.add(rank);
            }
        }
        if (unique.contains(14) && unique.contains(2) && unique.contains(3)
                && unique.contains(4) && unique.contains(5)) {
            return 5;
        }
        for (int i = 0; i <= unique.size() - 5; i++) {
            if (unique.get(i) - unique.get(i + 4) == 4) {
                return unique.get(i);
            }
        }
        return 0;
    }

    private int rankWithCount(Map<Integer, Integer> counts, int count) {
        int result = 0;
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == count) {
                result = Math.max(result, entry.getKey());
            }
        }
        return result;
    }

    private int highestPair(Map<Integer, Integer> counts) {
        return rankWithCount(counts, 2);
    }

    private List<Integer> ranksWithCount(Map<Integer, Integer> counts, int count) {
        List<Integer> result = new ArrayList<>();
        for (Map.Entry<Integer, Integer> entry : counts.entrySet()) {
            if (entry.getValue() == count) {
                result.add(entry.getKey());
            }
        }
        result.sort(Collections.reverseOrder());
        return result;
    }

    private List<Integer> highestRanksExcluding(Map<Integer, Integer> counts, int count, int excluded) {
        List<Integer> result = new ArrayList<>();
        for (int rank : counts.keySet()) {
            if (rank != excluded && counts.get(rank) == count) {
                result.add(rank);
            }
        }
        result.sort(Collections.reverseOrder());
        return result;
    }

    private record PlayerScore(Player player, Score score) { }

    private static class Score implements Comparable<Score> {
        private final List<Integer> values;

        Score(int category, int... values) {
            this.values = new ArrayList<>();
            this.values.add(category);
            for (int value : values) {
                this.values.add(value);
            }
        }

        Score(int category, List<Integer> values) {
            this.values = new ArrayList<>();
            this.values.add(category);
            this.values.addAll(values);
        }

        @Override
        public int compareTo(Score other) {
            int length = Math.max(values.size(), other.values.size());
            for (int i = 0; i < length; i++) {
                int left = i < values.size() ? values.get(i) : 0;
                int right = i < other.values.size() ? other.values.get(i) : 0;
                if (left != right) {
                    return Integer.compare(left, right);
                }
            }
            return 0;
        }
    }
}

