## Group 66: Casino Practice MVP
**Team Members:** Michaela, Glenn, Will, Aidan

### Attribution Table

| Class / Test File | Team Member(s) | Contribution Details |
| `Card.java` | Michaela | Wrote core class logic, established immutable suit and rank fields. |
| `Player.java` | Michaela | Wrote fields and methods, managed chips, hand, and fold state. |
| `SessionStats.java` | Michaela | Wrote tracking logic for wins, losses, and total gain. |
| `HandEvaluator.java` | Michaela | Wrote method signatures and outlined poker hand ranking rules. |
| `LinkedQueue.java` | Aidan | wrote custom queue structure, GenAI-assisted Javadoc & boundary checks. |
| `PlayerAction.java` | Aidan | Wrote action rules and validation logic for betting/folding. |
| `GameSnapshot.java` | Will | Wrote immutable state fields and unmodifiable list safeguards. |
| `PokerApp.java` | Will | Wrote console UI, formatted snapshot rendering and text output. |
| `GameHandler.java` | Will | Wrote model controller logic, dealt hands, and managed overall game state. |
| `ComputerAction.java` | Will | Wrote basic AI decision logic for bot opponents. |
| `PokerController.java` | Will | Wrote main game loop, input scanner, and format validation. |
| `LinkedQueueTest.java` | Aidan | GenAI-assisted writing of JUnit 5 test cases and exception handling. |
| `PlayerActionTest.java`| Aidan | GenAI-assisted writing of rule validation and chip limit tests. |
| `ModelTests.java`      | Michaela | Wrote JUnit tests for Player state changes and SessionStats tracking. |
| `ControllerTests.java` | Will | Wrote JUnit tests verifying game loop state and AI action boundaries. |

---

### Next Steps
Based on our recent testing and review of the MVP, we have adjusted our scope and prioritized the following next steps:
* **AI Logic Overhaul:** We will upgrade `ComputerAction` so the bot makes independent, varied decisions based on game state rather than simply mimicking the player's moves.
* **Output Formatting:** We will rewrite the text output for `ComputerAction` to ensure it looks clean, human-readable, and fits naturally with the game's UI rather than appearing AI-generated.
* **Game Mechanics & Balancing:** We will adjust the starting chip counts to better balance the pacing of the game.
* **Pot Splits:** We will implement pot-splitting logic to accurately handle ties and shared winnings during the showdown phase.
* **Adding Blackjack:** We are exploring the potential addition of Blackjack as a secondary game mode within the Casino Practice application.
