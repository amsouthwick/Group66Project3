## Group 66: Casino Practice MVP
**Team Members:** Michaela, Glenn, Will, Aidan

### Attribution Table

| Class / Test File | Team Member(s) | Contribution Details |
| `HandEvaluator.java` | Michaela | Updated this logic |
| `PlayerAction.java` | Aidan | Updated this logic|
| `GameSnapshot.java` | Will | Entirely rewrote |
| `GameHandler.java` | Will | Updated to include ComputerAction logic |
| `ComputerAction.java` | Will | Wrote this class |
| `ModelTests.java`      | Michaela | Wrote JUnit tests for Player state changes and SessionStats tracking. |

---

### Next Steps
Based on our recent testing and review of the MVP, we have adjusted our scope and prioritized the following next steps:
* **Bot Logic:** `ComputerAction` is not perfect the bots fold too often.
* * **Connect Blackjack and Poker** right now they are separate packages, and code reusability is non existent.
* **Output Formatting:** We still need to rewrite the text output for our UI class to ensure it looks clean and fits naturally with the game's UI rather than appearing AI-generated.
* **Game Mechanics & Balancing:** We will adjust the starting chip counts to better balance the pacing of the game.
* **Adding Niche Blackjack Features:** Doubling down and splitting need to be added.
* **Adding Niche Blackjack Features:** Add testing for classes with missing tests

We are fully committing to a fully working poker game with working bot logic. We are dropping the idea for a craps game.

Our 3 minute demo will include a poker game demonstrating what the bots are capable of, and a blackjack game. We will show how if you lose 50 in blackjack, but gain 50 in Poker your overall session stats will not change.

Will will complete ComupterAction
Michaela will complete tests
Aiden will merge the two packages to promote code reuseabilty
Glenn will finalize blackjack features and help with testing for blackjack

---
Reflection
1. Design Absorption & Class Impact
Classes Touched: GameSnapshot, PlayerAction, BlackjackGame, HandController, DealerBot, and underlying LinkedQueue data structures.
D3 Prediction vs. Reality: Our initial separation of game rules and view rendering matched predictions, allowing us to integrate Blackjack and bot logic cleanly. However, we were surprised by how tricky it was to tune autonomous bot thresholds; early iterations were overly timid, requiring several adjustments to their decision-making logic.
2. Model/View Boundary & Layer Integrity
Boundary Maintenance: The core game logic and bot decision trees remained strictly encapsulated within the model. The view only interacted via immutable GameSnapshot objects and event callbacks without learning internal model mechanics.
Layer Ripples: Thread-safe state dispatchers were introduced to handle asynchronous bot turn delays without freezing the UI, keeping calculations isolated from the view.
3. Divergence from the D3 Plan
Signatures & Validation: Added explicit split() and doubleDown() method signatures and enhanced betting validation for chip state limits during splits.
Testing: Expanded our JUnit suite to cover linked-queue turn cycling and split-hand settlements.
4. Current Limitations & Unfinished Work
Bot Behavior: Bots are fully autonomous but still occasionally exhibit conservative, timid play during high-stakes scenarios.
Edge-Case Validation: Multi-split recursion under low-balance conditions requires stricter input validation.
Test Coverage: Integration test coverage for rapid multi-hand blackjack rendering is incomplete.
**What doesn't work yet**
As of this deliverable, a few mechanics still need refinement. Our `ComputerAction` AI is currently too simplistic and tends to just mimic the player rather than making independent, varied decisions based on pot odds. The text output formatting for these actions feels clunky and needs to be rewritten to look natural. Mechanically, we still need to implement pot-splitting logic for when two players have the exact same winning hand at showdown, and we need to tweak the starting chip counts for better game balance.
