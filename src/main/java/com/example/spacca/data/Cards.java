package com.example.spacca.data;

// First letter of the card name represents the number on the card
// Second letter of the card name represents the shape on the card
// Third letter of the card name represents the color of the card


/**
 * <u>SHAPE CODES:</u><br>
 * <ul>
 *   <li>s = star</li> <br>
 *   <li>t = triangle</li> <br>
 *   <li>c = circle</li> <br>
 *   <li>r = rectangle</li> <br>
 *   <li>d = double-piled-triangle</li>
 * </ul><br><br>
 *
 * <u>COLOR CODES:</u> <br>
 * <ul>
 *   <li>g = green</li> <br>
 *   <li>r = red</li> <br>
 *   <li>b = blue</li> <br>
 *   <li>y = yellow</li> <br>
 *   <li>o = orange</li> <br>
 *   <li>p = pink</li>
 * </ul>
 */

public class Cards {
    public static final int cardsPerPlayer = Configurations.numberOfCardsPerPlayer; // 4 or 8];
    public static final String[] cardNames = {
            "1cp", "1db", "1ry", "1sg", "1so", "1sr", "1tb", "1to",
            "2cp", "2db", "2ry", "2sg", "2so", "2sr", "2tb", "2to",
            "3cp", "3db", "3ry", "3sg", "3so", "3sr", "3tb", "3to",
            "4cp", "4db", "4ry", "4sg", "4so", "4sr", "4tb", "4to"
    };

    public static String discardPileCard = cardNames[(int) (Math.random() * cardNames.length)];
    public static String cardSelectedInLastTurn = null;
}
