package cards;
import java.util.Random;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author Henke
 */
public class Deck 
{
    private final String [] SUITS = {"Spades","Hearts","Clubs","Diamond"};
    private final String [] VALUES = {"2","3","4","5","6","7","8","9","10","Jack","Queen","King","Ace" }; 
    private Card [] cards;
    private int nr_of_cards;
    
    public Deck()
    {
        nr_of_cards = 52;
        cards = new Card[52];
        for(int suit = 0; suit < 4; suit++)
        {
            for(int value = 0; value < 13; value++)
            cards[suit*13+value] = new Card(SUITS[suit],VALUES[value]);
        }
    }
    public boolean find_card(String suit, String value)
    {
        boolean found = false;
        for(int ix = 0; ix < nr_of_cards; ix++)
        {
            if(cards[ix].getSuit().equals(suit) && cards[ix].getValue().equals(value))
            {
                found = true;
            }
        }
        return found;
    }
    public Card pick_card_from_deck(String suit, String value)
    {
        Card card = null;
        if(find_card(suit,value))
        {
            for(int ix = 0; ix < nr_of_cards; ix++)
            {
                if(cards[ix].getSuit().equals(suit) && cards[ix].getValue().equals(value))
                {
                    card = cards[ix];
                    cards[ix] = cards[--this.nr_of_cards];
                }
            }
        }
        else
        {
            System.out.println("Card not found");
        }
        return card;
    }
    public Card pick_card_from_deck_with_int(String suit, int int_value)
    {
        Card card = null;
        if(find_card(suit,VALUES[int_value]))
        {
            for(int ix = 0; ix < nr_of_cards; ix++)
            {
                if(cards[ix].getSuit().equals(suit) && cards[ix].getValue().equals(VALUES[int_value]))
                {
                    card = cards[ix];
                    cards[ix] = cards[--this.nr_of_cards];
                }
            }
        }
        else
        {
            System.out.println("Card not found");
        }
        return card;
    }
    
    public void pick_card_with_int_values(int suit_value, int int_value)
    {
        for(int ix = 0; ix < this.nr_of_cards; ix++)
        {
            if(this.cards[ix].getIntValue() == int_value && 
                    this.cards[ix].getSuitIntValue() == suit_value)
            {
                this.cards[ix] = this.cards[this.nr_of_cards-1];
                this.nr_of_cards--;
            }
        }
    }
    
    public Card peek_card_nr(int nr)
    {
        Card card = null;
        if(nr >= 0 && nr < this.nr_of_cards)
        {
            card = cards[nr];
        }
        return card;
    }
    
    public Card peek_random_card()
    {
        Card card = null;
        if(this.nr_of_cards > 0)
        {
            Random rand = new Random();
            card = cards[rand.nextInt(this.nr_of_cards)];
        }
        return card;
    }
    
    public Card pick_random_card_from_deck()
    {
        Card card = null;
        if(this.nr_of_cards > 0)
        {
            Random rand = new Random();
            int nr = rand.nextInt(this.nr_of_cards);
            card = cards[nr];
            cards[nr] = cards[--this.nr_of_cards];
        }
        return card;
    }
    
    public int getNrOfCards()
    {
        return this.nr_of_cards;
    }
    
    public String value_and_suit_toString(int ix)
    {
        String s = "";
        if(this.nr_of_cards >= 0 && ix < this.nr_of_cards)
        {
            s = "Value: " + this.cards[ix].getValue()+ " Suit: "+ this.cards[ix].getSuit();
        }
        return s;
    }
    
    public void shuffle()
    {
        int count = 0;
        while(count < 2000)
        {
            Random rand = new Random();
            int first_card = rand.nextInt(nr_of_cards);
            int second_card = rand.nextInt(nr_of_cards);
            while(second_card == first_card)
            {
                second_card = rand.nextInt(nr_of_cards);
            }
            Card temp_card = cards[first_card];
            cards[first_card] = cards[second_card];
            cards[second_card] = temp_card;
            
            count++;
        }
    }
}
