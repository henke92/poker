/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cards;

import java.util.Random;


/**
 *
 * @author Henke
 */
public class Engine 
{
    private int street;
    int player_value,opponent_value;
    
    
    Card [] board;
    Card [] player_cards;
    Card [] opponent_cards;
    Card [] best_player_cards;
    Card [] best_opp_cards;
    
    Card [] best_sim_player_cards;
    Card [] best_sim_opp_cards;
    
    Deck d;
    
    
    
    public Engine()
    {
        
    }
   
    
    
    public void new_hand()
    {
        street = 0;
        d = new Deck();
        d.shuffle();
        board = new Card[5];
        player_cards = new Card[2];
        opponent_cards = new Card[2];
        best_player_cards = new Card[5];
        best_opp_cards = new Card[5];
        player_value = -1;
        opponent_value = -1;
        
    }
    
    public Deck get_deck()
    {
        return this.d;
    }
    
    public int get_street()
    {
        return this.street;
    }
    
    public void set_street(int n)
    {
        this.street = n;
    }
    
    
    
    
    public void next_street()
    {
        deal_street(street);
        street++;
    }
    
    public int eval_simul_seven_hand(Card [] cards)
    {
        int non_ix_0 = 0;
        int non_ix_1 = 1;
        int largest_value = -1, current_value = 0;
        Card [] five_cards;
        
        int count = 0;
        while(non_ix_0 < 6)
        {
            count = 0;
            five_cards = new Card[5];
            for(int ix = 0; ix < 7; ix++)
            {
                if(non_ix_0 != ix && non_ix_1 != ix)
                {
                    five_cards[count++] = cards[ix];
                }
            }
            current_value = eval_five_hand(five_cards);
            if(current_value > largest_value)
            {
                largest_value = current_value;
            }
            
            if(non_ix_1+1 < 7)
            {
                non_ix_1++;
            }
            else
            {
                non_ix_0++;
                non_ix_1 = non_ix_0+1;
            }
        }
        return largest_value;
    }
    
    public boolean no_fold_pre_flop()
    {
        boolean status = false;
        if(this.get_player_card_value(0) == this.get_player_card_value(1))
        {
            status  = true;
        }
        if(this.get_player_card_suit(0) == this.get_player_card_suit(1))
        {
            status = true;
        }
        if(this.get_player_card_value(0) == 12 && this.get_opp_card_value(1) == 11 || this.get_player_card_value(0) == 11 && this.get_player_card_value(1) == 12)
        {
            status = true;
        }                        
                
        return status;
    }
    
    public void guess_winner(int street, double [] values)
    {
        values[0] = values[1] = values[2] = 0.0;
        
        {            
            Card [] seven_hand_player = new Card[7];
            Card [] seven_hand_opp = new Card[7];
            Card turn;
            Card river;
            
            Deck sim_deck = new Deck();
            
            if(street >= 1)
            {
                for(int z = 0; z < 3; z++)
                {
                    sim_deck.pick_card_with_int_values(board[z].getSuitIntValue(),board[z].getIntValue());
                }
            }
            if(street == 2)
            {
                sim_deck.pick_card_with_int_values(board[3].getSuitIntValue(), board[3].getIntValue());
            }
            for(int z = 0; z < 2; z++)
            {
                sim_deck.pick_card_with_int_values(player_cards[z].getSuitIntValue(), player_cards[z].getIntValue());
                sim_deck.pick_random_card_from_deck();
                //sim_deck.pick_card_with_int_values(opponent_cards[z].getSuitIntValue(), opponent_cards[z].getIntValue());
            }
            
            
            
            if(street >= 1)
            {
                for(int z = 0; z < 3; z++)
                {
                    seven_hand_player[z] = this.board[z];
                    seven_hand_opp[z] = this.board[z];
                }
            }
            else
            {
                for(int z = 0; z < 3; z++)
                {
                    Card c = sim_deck.pick_random_card_from_deck();
                    seven_hand_player[z] = c;
                    seven_hand_opp[z] = c;
                }
            }
            
            
            //shuffle
            sim_deck.shuffle();
            if(street <= 1)
            {
                turn = sim_deck.pick_random_card_from_deck();
                river = sim_deck.pick_random_card_from_deck();
                seven_hand_player[3] = turn;
                seven_hand_player[4] = river;
                seven_hand_opp[3] = turn;
                seven_hand_opp[4] = river;
            }
            else if(street == 2)
            {
                turn = this.board[3];
                river = sim_deck.pick_random_card_from_deck();
                seven_hand_player[3] = turn;
                seven_hand_opp[3] = turn;
                seven_hand_player[4] = river;
                seven_hand_opp[4] = river;
            }
            for(int z = 5; z < 7; z++)
            {
                seven_hand_player[z] = this.player_cards[z-5];
                seven_hand_opp[z] = this.opponent_cards[z-5];
            }
            
            
            
            best_player_cards = new Card[5];
            best_opp_cards = new Card[5];
            
            
            int player_val = eval_seven_cards_hand(seven_hand_player,0);
            int opponent_val = eval_seven_cards_hand(seven_hand_opp,1);
            if(player_val > opponent_val)
            {
                values[0] += 1.0;
            }
            else if(opponent_val > player_val)
            {
                values[1] += 1.0;
            }
            else if(opponent_val == player_val)
            {
                int val = eval_highest_card(player_val);
                if(val == 0)
                {
                    values[0] += 1.0;
                }
                else if(val == 1)
                {
                    values[1] += 1.0;
                }
                else
                {
                    values[2] += 1.0;
                }
            }
        }
    }
    
   
    public void simulate_winner(int street, double [] values)
    {
        
        values[0] = values[1] = values[2] = 0.0;
        
        for(int ix = 0; ix < 1000; ix++)
        {            
            Card [] seven_hand_player = new Card[7];
            Card [] seven_hand_opp = new Card[7];
            Card turn;
            Card river;
            
            Deck sim_deck = new Deck();
            
            if(street >= 1)
            {
                for(int z = 0; z < 3; z++)
                {
                    sim_deck.pick_card_with_int_values(board[z].getSuitIntValue(),board[z].getIntValue());
                }
            }
            if(street == 2)
            {
                sim_deck.pick_card_with_int_values(board[3].getSuitIntValue(), board[3].getIntValue());
            }
            for(int z = 0; z < 2; z++)
            {
                sim_deck.pick_card_with_int_values(player_cards[z].getSuitIntValue(), player_cards[z].getIntValue());
                sim_deck.pick_card_with_int_values(opponent_cards[z].getSuitIntValue(), opponent_cards[z].getIntValue());
            }
            
            
            if(street >= 1)
            {
                for(int z = 0; z < 3; z++)
                {
                    seven_hand_player[z] = this.board[z];
                    seven_hand_opp[z] = this.board[z];
                }
            }
            else
            {
                for(int z = 0; z < 3; z++)
                {
                    Card c = sim_deck.pick_random_card_from_deck();
                    seven_hand_player[z] = c;
                    seven_hand_opp[z] = c;
                }
            }
            
            
            //shuffle
            sim_deck.shuffle();
            if(street <= 1)
            {
                turn = sim_deck.pick_random_card_from_deck();
                river = sim_deck.pick_random_card_from_deck();
                seven_hand_player[3] = turn;
                seven_hand_player[4] = river;
                seven_hand_opp[3] = turn;
                seven_hand_opp[4] = river;
            }
            else if(street == 2)
            {
                turn = this.board[3];
                river = sim_deck.pick_random_card_from_deck();
                seven_hand_player[3] = turn;
                seven_hand_opp[3] = turn;
                seven_hand_player[4] = river;
                seven_hand_opp[4] = river;
            }
            for(int z = 5; z < 7; z++)
            {
                seven_hand_player[z] = this.player_cards[z-5];
                seven_hand_opp[z] = this.opponent_cards[z-5];
            }
            
            
            
            best_player_cards = new Card[5];
            best_opp_cards = new Card[5];
            
            
            int player_val = eval_seven_cards_hand(seven_hand_player,0);
            int opponent_val = eval_seven_cards_hand(seven_hand_opp,1);
            if(player_val > opponent_val)
            {
                values[0] += 1.0;
            }
            else if(opponent_val > player_val)
            {
                values[1] += 1.0;
            }
            else if(opponent_val == player_val)
            {
                int val = eval_highest_card(player_val);
                if(val == 0)
                {
                    values[0] += 1.0;
                }
                else if(val == 1)
                {
                    values[1] += 1.0;
                }
                else
                {
                    values[2] += 1.0;
                }
            }
        }
    }
  
    
    
    public void deal_street(int n)
    {
        switch(street)
        {
            case 0:
                deal_pre_flop();
                break;
            case 1:
                deal_flop();
                break;
            case 2:
                deal_turn();
                break;
            case 3:
                deal_river();
                break;
            case 4:
                //eval
                break;
            default:
                break;
        }
    }
    
    
    public void timeDelay(long t) 
    {
    try {
        Thread.sleep(t);
    } catch (InterruptedException e) 
    {}
}
    
    
    public boolean board_high(int street)
    {
        boolean status = false;
        
        switch(street)
        {
            case 1:
                //pair
                if(board[0].getIntValue() == board[1].getIntValue())
                {
                    status = true;
                }
                if(board[0].getIntValue() == board[2].getIntValue())
                {
                    status = true;
                }
                if(board[1].getIntValue() == board[2].getIntValue())
                {
                    status = true;
                }
                
                //flush
                if(board[0].getSuitIntValue() == board[1].getSuitIntValue() && board[0].getSuitIntValue() == board[2].getSuitIntValue())
                {
                    status = true;
                }
                
                //straight
                if(board[0].getIntValue()+1 == board[1].getIntValue())
                {
                    if(board[1].getIntValue()+1 == board[2].getIntValue())
                    {
                        status = true;
                    }
                    if(board[0].getIntValue() == 0 && board[2].getIntValue() == 12)
                    {
                        status = true;
                    }
                }
                
                break;
            case 2:
                Card [] temp = new Card[4];
                for(int ix = 0; ix < 4; ix++)
                {
                    temp[ix] = board[ix];
                }
                bubble_sort(temp,4);
                
                //pair
                if(temp[0].getIntValue() == temp[1].getIntValue())
                {
                    status = true;
                }
                if(temp[1].getIntValue() == temp[2].getIntValue())
                {
                    status = true;
                }
                if(temp[2].getIntValue() == temp[3].getIntValue())
                {
                    status = true;
                }
                
                //straight
                if(temp[0].getIntValue()+1 == temp[1].getIntValue())
                {
                    if(temp[1].getIntValue()+1 == temp[2].getIntValue())
                    {
                        status = true;
                    }
                    if((temp[2].getIntValue() == 12 || temp[3].getIntValue() == 12) && temp[0].getIntValue() == 0)
                    {
                        status = true;
                    }
                }
                
                //flush
                
                if(temp[0].getSuitIntValue() == temp[1].getSuitIntValue())
                {
                    if(temp[0].getSuitIntValue() == temp[2].getSuitIntValue())
                    {
                        status = true;
                    }
                    if(temp[0].getSuitIntValue() == temp[3].getSuitIntValue())
                    {
                        status = true;
                    }
                }
                if(temp[0].getSuitIntValue() == temp[2].getSuitIntValue())
                {
                    if(temp[0].getSuitIntValue() == temp[3].getSuitIntValue())
                    {
                        status = true;
                    }
                }
                if(temp[1].getSuitIntValue() == temp[2].getSuitIntValue())
                {
                    if(temp[1].getSuitIntValue() == temp[3].getSuitIntValue())
                    {
                        status = true;
                    }
                }
                
                break;
            case 3:
                if(eval_five_hand(board) >= 1)
                {
                    status = true;
                }
                break;
        }
        
        return status;
    }
    

    public int get_player_card_value(int n)
    {
        int value = -1;
        if(n == 0 || n == 1)
        {
            value = this.player_cards[n].getIntValue();
        }
        return value;
    }
    
    public int get_player_card_suit(int n)
    {
        int value = -1;
        if(n == 0 || n == 1)
        {
            value = this.player_cards[n].getSuitIntValue();
        }
        return value;
    }
    
    public int get_opp_card_value(int n)
    {
        int value = -1;
        if(n == 0 || n == 1)
        {
            value = this.opponent_cards[n].getIntValue();
        }
        return value;
    }
    
    public int get_opp_card_suit(int n)
    {
        int value = -1;
        if(n == 0 || n == 1)
        {
            value = this.opponent_cards[n].getSuitIntValue();
        }
        return value;
    }
    
    public int get_board_value(int n)
    {
        int value = -1;
        if(n >= 0 && n < 5)
        {
            return this.board[n].getIntValue();
        }
        return value;
    }
    
    public int get_board_suit(int n)
    {
        int value = -1;
        if(n >= 0 && n < 5)
        {
            return this.board[n].getSuitIntValue();
        }
        return value;
    }
    
    
    
    public void deal_pre_flop()
    {
        player_cards[0] = d.pick_random_card_from_deck();
        player_cards[1] = d.pick_random_card_from_deck();
        opponent_cards[0] = d.pick_random_card_from_deck();
        opponent_cards[1] = d.pick_random_card_from_deck();
    }
    
    public void deal_flop()
    {
        for(int ix = 0; ix < 3; ix++)
        {
            board[ix] = d.pick_random_card_from_deck();
        }
    }
    
    public void deal_turn()
    {
        board[3] = d.pick_random_card_from_deck();
    }
    
    public void deal_river()
    {
        board[4] = d.pick_random_card_from_deck();
    }
    
    
    
    
    
    
    //Eval functions
    
     public void bubble_sort(Card [] card, int size)
    {
        int n = size;
        boolean swapped = false;
        do
        {
            swapped = false;
            for(int i = 1; i < n; i++)
            {
                if(card[i-1].getIntValue() > card[i].getIntValue())
                {
                    Card temp = card[i];
                    card[i] = card[i-1];
                    card[i-1] = temp;
                    swapped = true;
                }
            }
        }while(swapped);
    }
    
    public boolean eval_straight(Card [] cards)
    {
        boolean status = false;
        if(cards[0].getIntValue() + 1 == cards[1].getIntValue())
        {
            if(cards[1].getIntValue() + 1 == cards[2].getIntValue())
            {
                if(cards[2].getIntValue() + 1 == cards[3].getIntValue())
                {
                    if(cards[0].getIntValue() == 0 && cards[4].getIntValue() == 12)
                    {
                        status = true;
                    }
                    if(cards[3].getIntValue() + 1 == cards[4].getIntValue())
                    {
                        status = true;
                    }
                }
            }
        }
        return status;
    }
    
    public boolean eval_flush(Card [] cards)
    {
        boolean status = false;
        if(cards[1].getSuit().equals(cards[0].getSuit()))
        {
            if(cards[2].getSuit().equals(cards[0].getSuit()))
            {
                if(cards[3].getSuit().equals(cards[0].getSuit()))
                {
                    if(cards[4].getSuit().equals(cards[0].getSuit()))
                    {
                        status = true;
                    }
                }
            } 
        }
        return status;
    }
    
    public boolean eval_full_house(Card [] cards)
    {
        boolean status = false;
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            if(cards[1].getIntValue() == cards[2].getIntValue())
            {
                if(cards[3].getIntValue() == cards[4].getIntValue())
                {
                    status = true;
                }
            }
        }
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            if(cards[2].getIntValue() == cards[3].getIntValue())
            {
                if(cards[3].getIntValue() == cards[4].getIntValue())
                {
                    status = true;
                }
            }
        }
        return status;
    }
    
    public boolean eval_four_of_kind(Card [] cards)
    {
        boolean status = false;
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            if(cards[1].getIntValue() == cards[2].getIntValue())
            {
                if(cards[2].getIntValue() == cards[3].getIntValue())
                {
                    status = true;
                }
            }
        }
        else
        {
            if(cards[1].getIntValue() == cards[2].getIntValue())
            {
                if(cards[2].getIntValue() == cards[3].getIntValue())
                {
                    if(cards[3].getIntValue() == cards[4].getIntValue())
                    {
                        status = true;
                    }
                }
            }
        }
        return status;
    }
    
    public boolean eval_three_kind(Card[] cards)
    {
        boolean status = false;
        
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            if(cards[1].getIntValue() == cards[2].getIntValue())
            {
                status = true;
            }
        }
        if(cards[1].getIntValue() == cards[2].getIntValue())
        {
            if(cards[2].getIntValue() == cards[3].getIntValue())
            {
                status = true;
            }
        }
        if(cards[2].getIntValue() == cards[3].getIntValue())
        {
            if(cards[3].getIntValue() == cards[4].getIntValue())
            {
                status = true;
            }
        }
        
        return status;
    }
    
    public boolean eval_two_pair(Card [] cards)
    {
        boolean status = false;
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            if(cards[2].getIntValue() == cards[3].getIntValue())
            {
                status = true;
            }
            if(cards[3].getIntValue() == cards[4].getIntValue())
            {
                status = true;
            }
        }
        if(cards[1].getIntValue() == cards[2].getIntValue())
        {
            if(cards[3].getIntValue() == cards[4].getIntValue())
            {
                status = true;
            }
        }
        return status;
    }
    
    public boolean eval_pair(Card [] cards)
    {
        boolean status = false;
        if(cards[0].getIntValue() == cards[1].getIntValue())
        {
            status = true;
        }
        if(cards[1].getIntValue() == cards[2].getIntValue())
        {
            status = true;
        }
        if(cards[2].getIntValue() == cards[3].getIntValue())
        {
            status = true;
        }
        if(cards[3].getIntValue() == cards[4].getIntValue())
        {
            status = true;
        }
        return status;
    }
    
    
    public int eval_seven_cards_hand(Card [] cards, int player)
    {
        int non_ix_0 = 0;
        int non_ix_1 = 1;
        int largest_value = -1, current_value = 0;
        Card [] five_cards;
        
        int count = 0;
        while(non_ix_0 < 6)
        {
            count = 0;
            five_cards = new Card[5];
            for(int ix = 0; ix < 7; ix++)
            {
                if(non_ix_0 != ix && non_ix_1 != ix)
                {
                    five_cards[count++] = cards[ix];
                }
            }
            current_value = eval_five_hand(five_cards);
            if(current_value > largest_value)
            {
                if(player == 0)
                {
                    best_player_cards = five_cards;
                }
                else
                {
                    best_opp_cards = five_cards;
                }
                largest_value = current_value;
            }
            
            if(non_ix_1+1 < 7)
            {
                non_ix_1++;
            }
            else
            {
                non_ix_0++;
                non_ix_1 = non_ix_0+1;
            }
            
        }
        return largest_value;
    }
    
    public int eval_five_hand(Card [] cards )
    {
        int eval_value = 0;
        boolean straight = false;
        boolean flush = false;
        bubble_sort(cards,5);
        straight = eval_straight(cards);
        flush = eval_flush(cards);
        
        if(cards[3].getIntValue() == 11 && cards[4].getIntValue() == 12 && straight && flush)
        {
            eval_value = 9;
        }
        else if(straight && flush)
        {
            eval_value = 8;
        }
        else if(eval_four_of_kind(cards))
        {
            eval_value = 7;
        }
        else if(eval_full_house(cards))
        {
            eval_value = 6;
        }
        else if(flush)
        {
            eval_value = 5;
        }
        else if(straight)
        {
            eval_value = 4;
        }
        else if(eval_three_kind(cards))
        {
            eval_value = 3;
        }
        else if(eval_two_pair(cards))
        {
            eval_value = 2;
        }
        else if(eval_pair(cards))
        {
            eval_value = 1;
        }
        else
        {
            eval_value = 0;
        }
        
        
        return eval_value;
    }
    
    public int eval_highest_card(int eval_value)
    {
        Card [] temp = new Card[5];
        bubble_sort(best_player_cards,5);
        bubble_sort(best_opp_cards,5);
        
        int player_high = -1;
        int opp_high = -1;
        
        int winner = -1;
        switch(eval_value)
        {
            case 9:
                winner = -1;
                break;
            case 8:
                if(best_player_cards[4].getIntValue() > best_opp_cards[4].getIntValue())
                {
                    winner = 0;
                }
                else if(best_opp_cards[4].getIntValue() > best_player_cards[4].getIntValue())
                {
                    winner = 1;
                }
                break;
            case 7:
                if(best_player_cards[0].getIntValue() == best_player_cards[1].getIntValue())
                {
                    player_high = best_player_cards[0].getIntValue();
                }
                if(best_player_cards[3].getIntValue() == best_player_cards[4].getIntValue())
                {
                    player_high = best_player_cards[3].getIntValue();
                }
                if(best_opp_cards[0].getIntValue() == best_opp_cards[1].getIntValue())
                {
                    opp_high = best_opp_cards[0].getIntValue();
                }
                if(best_opp_cards[3].getIntValue() == best_opp_cards[4].getIntValue())
                {
                    opp_high = best_opp_cards[3].getIntValue();
                }
                if(player_high > opp_high)
                {
                    winner = 0;
                }
                else if(opp_high > player_high)
                {
                    winner = 1;
                }
                else if(player_high == opp_high)
                {
                    if(best_player_cards[0].getIntValue() != best_player_cards[1].getIntValue())
                    {
                        player_high = best_player_cards[0].getIntValue();
                    }
                    else
                    {
                        player_high = best_player_cards[4].getIntValue();
                    }
                    if(best_opp_cards[0].getIntValue() != best_opp_cards[1].getIntValue())
                    {
                        opp_high = best_opp_cards[0].getIntValue();
                    }
                    else
                    {
                        opp_high = best_opp_cards[4].getIntValue();
                    }
                    if(player_high > opp_high)
                    {
                        winner = 0;
                    }
                    else if(opp_high > player_high)
                    {
                        winner = 1;
                    }
                }
                break;
            case 6:
                if(best_player_cards[1].getIntValue() == best_player_cards[2].getIntValue())
                {
                    player_high = best_player_cards[1].getIntValue();
                }
                else
                {
                    player_high = best_player_cards[3].getIntValue();
                }
                if(best_opp_cards[1].getIntValue() == best_opp_cards[2].getIntValue())
                {
                    opp_high = best_opp_cards[1].getIntValue();
                }
                else
                {
                    opp_high = best_opp_cards[3].getIntValue();
                }
                
                if(player_high > opp_high)
                {
                    winner = 0;
                }
                else if(opp_high > player_high)
                {
                    winner = 1;
                }
                else if(player_high == opp_high)
                {
                    if(best_player_cards[1].getIntValue() != best_player_cards[2].getIntValue())
                    {
                        player_high = best_player_cards[1].getIntValue();
                    }
                    else
                    {
                        player_high = best_player_cards[4].getIntValue();
                    }
                    if(best_opp_cards[1].getIntValue() != best_opp_cards[2].getIntValue())
                    {
                        opp_high = best_opp_cards[1].getIntValue();
                    }
                    else
                    {
                        opp_high = best_opp_cards[4].getIntValue();
                    }
                    
                    if(player_high > opp_high)
                    {
                        winner = 0;
                    }
                    else if(opp_high > player_high)
                    {
                        winner = 1;
                    }
                }
                break;
            case 5:
                if(best_player_cards[4].getIntValue() > best_opp_cards[4].getIntValue())
                {
                    winner = 0;
                }
                else if(best_opp_cards[4].getIntValue() > best_player_cards[4].getIntValue())
                {
                    winner = 1;
                }
                break;
            case 4:
                if(best_player_cards[4].getIntValue() > best_opp_cards[4].getIntValue())
                {
                    winner = 0;
                }
                else if(best_opp_cards[4].getIntValue() > best_player_cards[4].getIntValue())
                {
                    winner = 1;
                }
                break;
            case 3:
                
                int second_player,third_player;
                int second_opp,third_opp;
                if(best_player_cards[1].getIntValue() == best_player_cards[2].getIntValue())
                {
                    player_high = best_player_cards[1].getIntValue();
                    second_player = best_player_cards[4].getIntValue();
                    third_player = best_player_cards[3].getIntValue();
                }
                else
                {
                    player_high = best_player_cards[2].getIntValue();
                    second_player = best_player_cards[1].getIntValue();
                    third_player = best_player_cards[0].getIntValue();
                    
                }
                if(best_opp_cards[1].getIntValue() == best_opp_cards[2].getIntValue())
                {
                    opp_high = best_opp_cards[1].getIntValue();
                    second_opp = best_opp_cards[4].getIntValue();
                    third_opp = best_opp_cards[3].getIntValue();
                }
                else
                {
                    opp_high = best_opp_cards[2].getIntValue();
                    second_opp = best_opp_cards[1].getIntValue();
                    third_opp = best_opp_cards[0].getIntValue();
                }
                if(player_high > opp_high)
                {
                    winner = 0;
                }
                else if(opp_high > player_high)
                {
                    winner = 1;
                }
                else if(player_high == opp_high)
                {
                    if(second_player > second_opp)
                    {
                        winner = 0;
                    }
                    else if(second_opp > second_player)
                    {
                        winner = 1;
                    }
                    else if(second_player == second_opp)
                    {
                        if(third_player > third_opp)
                        {
                            winner = 0;
                        }
                        else if(third_opp > third_player)
                        {
                            winner = 1;
                        }
                    }
                }
                break;
            case 2:
                
                int player_second;
                int opp_second;
                int player_kicker;
                int opp_kicker;
                
                if(best_player_cards[4].getIntValue() == best_player_cards[3].getIntValue())
                {
                    player_high = best_player_cards[4].getIntValue();
                    if(best_player_cards[2].getIntValue() == best_player_cards[1].getIntValue())
                    {
                        player_second = best_player_cards[1].getIntValue();
                        player_kicker = best_player_cards[0].getIntValue();
                    }
                    else
                    {
                        player_second = best_player_cards[0].getIntValue();
                        player_kicker = best_player_cards[2].getIntValue();
                    }
                }
                else
                {
                    player_high = best_player_cards[3].getIntValue();
                    player_second = best_player_cards[0].getIntValue();
                    player_kicker = best_player_cards[4].getIntValue();
                }
                if(best_opp_cards[4].getIntValue() == best_opp_cards[3].getIntValue())
                {
                    opp_high = best_opp_cards[4].getIntValue();
                    if(best_opp_cards[2].getIntValue() == best_opp_cards[1].getIntValue())
                    {
                        opp_second = best_opp_cards[1].getIntValue();
                        opp_kicker = best_opp_cards[0].getIntValue();
                    }
                    else
                    {
                        opp_second = best_opp_cards[0].getIntValue();
                        opp_kicker = best_opp_cards[2].getIntValue();
                    }
                }
                else
                {
                    opp_high = best_opp_cards[3].getIntValue();
                    opp_second = best_opp_cards[0].getIntValue();
                    opp_kicker = best_opp_cards[4].getIntValue();
                }
                
                if(player_high > opp_high)
                {
                    winner = 0;
                }
                else if(opp_high > player_high)
                {
                    winner = 1;
                }
                else if(opp_high == player_high)
                {
                    if(player_second > opp_second)
                    {
                        winner = 0;
                    }
                    else if(opp_second > player_second)
                    {
                        winner = 1;
                    }
                    else if(player_second == opp_second)
                    {
                        if(player_kicker > opp_kicker)
                        {
                            winner = 0;
                        }
                        else if(opp_kicker > player_kicker)
                        {
                            winner = 1;
                        }
                    }
                }
                
                
                break;
            case 1:
                
                int pl_first,pl_second,pl_third;
                int opp_first,opp_third;
                
                pl_first = -1;
                pl_second = -1;
                pl_third = -1;
                opp_first = -1;
                opp_second = -1;
                opp_third = -1;
                
                if(best_player_cards[0].getIntValue() == best_player_cards[1].getIntValue())
                {
                    player_high = best_player_cards[0].getIntValue();
                    pl_first = best_player_cards[4].getIntValue();
                    pl_second = best_player_cards[3].getIntValue();
                    pl_third = best_player_cards[2].getIntValue();
                }
                else if(best_player_cards[1].getIntValue() == best_player_cards[2].getIntValue())
                {
                    player_high = best_player_cards[1].getIntValue();
                    pl_first = best_player_cards[4].getIntValue();
                    pl_second = best_player_cards[3].getIntValue();
                    pl_third = best_player_cards[0].getIntValue();
                }
                else if(best_player_cards[2].getIntValue() == best_player_cards[3].getIntValue())
                {
                    player_high = best_player_cards[2].getIntValue();
                    pl_first = best_player_cards[4].getIntValue();
                    pl_second = best_player_cards[1].getIntValue();
                    pl_third = best_player_cards[0].getIntValue();
                }
                    
                else if(best_player_cards[3].getIntValue() == best_player_cards[4].getIntValue())
                {
                    player_high = best_player_cards[3].getIntValue();
                    pl_first = best_player_cards[2].getIntValue();
                    pl_second = best_player_cards[1].getIntValue();
                    pl_third = best_player_cards[0].getIntValue();
                }
                
                
                if(best_opp_cards[0].getIntValue() == best_opp_cards[1].getIntValue())
                {
                    opp_high = best_opp_cards[0].getIntValue();
                    opp_first = best_opp_cards[4].getIntValue();
                    opp_second = best_opp_cards[3].getIntValue();
                    opp_third = best_opp_cards[2].getIntValue();
                }
                if(best_opp_cards[1].getIntValue() == best_opp_cards[2].getIntValue())
                {
                    opp_high = best_opp_cards[1].getIntValue();
                    opp_first = best_opp_cards[4].getIntValue();
                    opp_second = best_opp_cards[3].getIntValue();
                    opp_third = best_opp_cards[0].getIntValue();
                }
                if(best_opp_cards[2].getIntValue() == best_opp_cards[3].getIntValue())
                {
                    opp_high = best_opp_cards[2].getIntValue();
                    opp_first = best_opp_cards[4].getIntValue();
                    opp_second = best_opp_cards[1].getIntValue();
                    opp_third = best_opp_cards[0].getIntValue();
                }
                if(best_opp_cards[3].getIntValue() == best_opp_cards[4].getIntValue())
                {
                    opp_high = best_opp_cards[3].getIntValue();
                    opp_first = best_opp_cards[2].getIntValue();
                    opp_second = best_opp_cards[1].getIntValue();
                    opp_third = best_opp_cards[0].getIntValue();
                    
                }
               
                
                
                
                if(player_high > opp_high)
                {
                    winner = 0;
                }
                else if(opp_high > player_high)
                {
                    winner = 1;
                }
                else if(player_high == opp_high)
                {
                    if(pl_first > opp_first)
                    {
                        winner = 0;
                    }
                    else if(opp_first > pl_first)
                    {
                        winner = 1;
                    }
                    else if(pl_first == opp_first)
                    {
                        if(pl_second > opp_second)
                        {
                            winner = 0;
                        }
                        else if(opp_second > pl_second)
                        {
                            winner = 1;
                        }
                        else if(pl_second == opp_second)
                        {
                            if(pl_third > opp_third)
                            {
                                winner = 0;
                            }
                            else if(opp_third > pl_third)
                            {
                                winner = 1;
                            }
                        }
                    }
                }
                break;
            case 0:
                
                if(best_player_cards[4].getIntValue() > best_opp_cards[4].getIntValue())
                {
                    winner = 0;
                }
                else if(best_opp_cards[4].getIntValue() > best_player_cards[4].getIntValue())
                {
                    winner = 1;
                }
                else if(best_player_cards[4].getIntValue() == best_opp_cards[4].getIntValue())
                {
                    if(best_player_cards[3].getIntValue() > best_opp_cards[3].getIntValue())
                    {
                        winner = 0;
                    }
                    else if(best_opp_cards[3].getIntValue() > best_player_cards[3].getIntValue())
                    {
                        winner = 1;
                    }
                    else if(best_player_cards[3].getIntValue() == best_opp_cards[3].getIntValue())
                    {
                        if(best_player_cards[2].getIntValue() > best_opp_cards[2].getIntValue())
                        {
                            winner = 0;
                        }
                        else if(best_opp_cards[2].getIntValue() > best_player_cards[2].getIntValue())
                        {
                            winner = 1;
                        }
                        else if(best_player_cards[2].getIntValue() == best_opp_cards[2].getIntValue())
                        {
                            if(best_player_cards[1].getIntValue() > best_opp_cards[1].getIntValue())
                            {
                                winner = 0;
                            }
                            else if(best_opp_cards[1].getIntValue() > best_player_cards[1].getIntValue())
                            {
                                winner = 1;
                            }
                            else if(best_player_cards[1].getIntValue() == best_opp_cards[1].getIntValue())
                            {
                                if(best_player_cards[0].getIntValue() > best_opp_cards[0].getIntValue())
                                {
                                    winner = 0;
                                }
                                else if(best_opp_cards[0].getIntValue() > best_player_cards[0].getIntValue())
                                {
                                    winner = 1;
                                }
                            }
                        }
                    }
                }
                
                break;                
            default:
            break;
        }
        return winner;
    }
    
    public int eval_player_cards_end()
    {
        player_value = -1;
         //Player
        Card eval_cards [] = new Card[7];
        for(int ix = 0; ix < 5; ix++)
        {
            eval_cards[ix] = board[ix];
        }
        eval_cards[5] = player_cards[0];
        eval_cards[6] = player_cards[1];
        
        player_value = eval_seven_cards_hand(eval_cards,0);
        return player_value;
    }
    
    public int eval_opponent_cards_end()
    {
        //Opponent
        opponent_value = -1;
        Card eval_cards [] = new Card[7];
        eval_cards = new Card[7];
        for(int ix = 0; ix < 5; ix++)
        {
            eval_cards[ix] = board[ix];
        }
        eval_cards[5] = opponent_cards[0];
        eval_cards[6] = opponent_cards[1];
        
        opponent_value = eval_seven_cards_hand(eval_cards,1);
        return opponent_value;
        
    }
    
     public int eval_flop(int player)
    {
        int value;
        
        Card [] c = new Card[5];
        for(int ix = 0; ix < 3; ix++)
        {
            c[ix] = board[ix];
        }
        if(player == 0)
        {
            c[3] = player_cards[0];
            c[4] = player_cards[1];
        }
        else
        {
            c[3] = opponent_cards[0];
            c[4] = opponent_cards[1];
        }
        
        value = eval_five_hand(c);
        
        return value;
    }
    
     public int eval_turn(int player)
    {
        int highest_value = -1;
        int current_value;
        Card [] c = new Card[6];
        for(int ix = 0; ix < 4; ix++)
        {
            c[ix] = board[ix];
        }
        if(player == 0)
        {
            c[4] = player_cards[0];
            c[5] = player_cards[1];
        }
        else
        {
            c[4] = opponent_cards[0];
            c[5] = opponent_cards[1];
        }
        
        
        for(int ix = 0; ix < 5; ix++)
        {
            Card [] temp = new Card[5];
            for(int z = 0; z < 5; z++)
            {
                temp[z] = c[z];
            }
            temp[ix] = c[5];
            current_value = eval_five_hand(temp);
            if(current_value > highest_value)
            {
                highest_value = current_value;
            }
        }
        Card [] temp = new Card[5];
        for(int ix = 0 ; ix < 5; ix++)
        {
            temp[ix] = c[ix];
        }
        current_value = eval_five_hand(temp);
        if(current_value > highest_value)
        {
            highest_value = current_value;
        }
        
        return highest_value;
    }
    
    public void eval_river()
    {
        eval_player_cards_end();
        eval_opponent_cards_end();
        if(player_value > opponent_value)
        {
            //System.out.println("Player wins");
        }
        else if(opponent_value > player_value)
        {
            //System.out.println("Computer wins");
        }
        else if(player_value == opponent_value)
        {
            int state = eval_highest_card(player_value);
            if(state == 0)
            {
                //System.out.println("Player wins");
            }
            else if(state == 1)
            {
                //System.out.println("COmputer wins");
            }
            else
            {
                //System.out.println("Split pot");
            }
        }
    }
    
    
    public int get_river_winner()
    {
        int w = -1;
        eval_player_cards_end();
        eval_opponent_cards_end();
        if(player_value > opponent_value)
        {
            w = 0;
        }
        else if(opponent_value > player_value)
        {
            w = 1;
        }
        else if(player_value == opponent_value)
        {
            int val = eval_highest_card(player_value);
            if(val == 0)
            {
                w = 0;
            }
            else if(val == 1)
            {
                w = 1;
            }
            else
            {
                w = -1;
            }
        }
        return w;
    }
  
}
