package cards;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Henke
 */
public class Card 
{
    private final String [] SUITS = {"Hearts","Spades","Diamond","Clubs"};
    private final String [] VALUES = {"2","3","4","5","6","7","8","9","10","Jack","Queen","King","Ace" }; 
    private String suit;
    private String value;
    private boolean in_deck;
    
    public Card()
    {
        this.suit = null;
        this.value = null;
        this.in_deck = true;
    }
    public Card(String suit, String value)
    {
        this.suit = suit;
        this.value = value;
        this.in_deck = true;
    }
    
    public void setValueByInt(int n)
    {
        if(n >= 0 && n < 13)
        {
            this.value = VALUES[n];
        }
    }
    
    public void setSuit(String suit)
    {
        this.suit = suit;
    }
    public void setValue(String value)
    {
        this.value = value;
    }
    public String getSuit() 
    {
        return this.suit;
    }
    public String getValue()
    {
        return this.value;
    }
    public int getIntValue()
    {
        int value = -1;
        for(int ix = 0; ix < 13; ix++)
        {
            if(this.value.equals(VALUES[ix]))
            {
                value = ix;
            }
        }
        return value;
    }
    public int getSuitIntValue()
    {
        int value = -1;
        for(int ix = 0; ix < 4; ix++)
        {
            if(this.suit.equals(SUITS[ix]))
            {
                value = ix;
            }
        }
        return value;
    }
    public void set_deck_status(boolean status)
    {
        this.in_deck = status;
    }
    public boolean in_deck()
    {
        return this.in_deck;
    }
    
    public String value_and_suit_toString()
    {
        String s = "";
        if(this.suit != null && this.value != null)
        {
            s = this.value + " of " + this.suit;
        }
        return s;
    }
    
    /*
    public int getSuitInteger()
    {
        
    }
    public int getValueInteger()
    {
        
    }
    */
           
}
