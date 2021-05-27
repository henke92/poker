/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cards;

/**
 *
 * @author Henke
 */
public class BetHandler 
{
    private int player_markers;
    private int comp_markers;
    private int pot;
    private int dealer;     //0 computer, 1//player
    private int player_turn;    //0 computer, 1//player
    private int street;     //0 pre, 1 flop, 2 turn, 3 river, (4 eval)
    private int player_in_pot;
    private int comp_in_pot;
    private boolean street_is_over;
    private boolean hand_is_over;
    private boolean all_in;
    private int action_count;
    private int winner = -1; //0 comp , 1 player
    
    
    public BetHandler()
    {
        this.player_markers = this.comp_markers  = 1500;
        this.pot = 0;
        this.dealer = 0;
        this.player_turn = 0;
        this.street = 0;
        this.action_count = 0;
    }
    
    public int get_winner()
    {
        return winner;
    }
    
    public boolean game_is_over()
    {
        boolean status = false;
        if(this.street <= 1)
        {
            if(this.player_markers <= 0 || this.comp_markers <= 0)
            {
                status = true;
            }
        }
        return status;
    }
    
    public void set_street(int street)
    {
        this.street = street;
    }
    
    public int get_street()
    {
        return this.street;
    }
    
    public int get_player_turn()
    {
        return this.player_turn;
    }
    
    public int get_maximum_comp_bet()
    {
        int value = -1;
        if(comp_is_bet())
        {
            value = this.comp_markers;
        }
        return value;
    }
    
    public int get_maximum_player_bet()
    {
        int value = -1;
        if(player_is_bet())
        {
            value = this.player_markers;
        }
        return value;
    }
    
    public int get_minimum_comp_bet()
    {
        int value = -1;
        if(comp_is_bet())
        {
            value = (this.player_in_pot-this.comp_in_pot)+20;
        }
        if(value > this.comp_markers)
        {
            value = this.comp_markers;
        }
        return value;
    }
    
    public int get_minimum_player_bet()
    {
        int value = -1;
        if(player_is_bet())
        {
            value = (this.comp_in_pot-this.player_in_pot)+20;
        }
        if(value > this.player_markers)
        {
            value = this.player_markers;
        }
        return value;
    }
    
    public boolean player_is_bet()
    {
        boolean status = false;
        if(this.player_markers > (this.comp_in_pot-this.player_in_pot))
        {
            status = true;
        }
        if(this.player_markers - this.player_in_pot > this.comp_in_pot && this.comp_markers == 0)
        {
            status = false;
        }
        if(this.player_markers > this.comp_in_pot-this.player_in_pot && this.comp_markers == 0)
        {
            status = false;
        }
        return status;
    }
    
    public boolean comp_is_bet()
    {
        boolean status = false;
        if(this.comp_markers > (this.player_in_pot - this.comp_in_pot))
        {
            status = true;
        }
        if(this.comp_markers - this.comp_in_pot > this.player_in_pot && this.player_markers == 0)
        {
            status = false;
        }
        if(this.comp_markers > this.player_in_pot-this.comp_in_pot && this.player_markers == 0)
        {
            status = false;
        }
        return status;
    }
    
    public boolean comp_is_call()
    {
        boolean status = false;
        if(this.comp_in_pot < this.player_in_pot)
        {
            status = true;
        }
        return status;
    }
    
    public boolean player_is_call()
    {
        boolean status = false;
        if(this.player_in_pot < this.comp_in_pot)
        {
            status = true;
        }
        return status;
    }
    
    public boolean is_check()
    {
        boolean status = false;
        if(this.player_in_pot == this.comp_in_pot)
        {
            status = true;
        }
        return status;
    }
    
    public boolean hand_is_over()
    {
        return this.hand_is_over;
    }
    
    public boolean street_is_over()
    {
        return this.street_is_over;
    }
    
    public void add_eval_end_pot(int winner)    //0 comp, 1 player, -1 draw
    {
        if(winner == 0)
        {
            this.player_markers += this.pot;
        }
        else if(winner == 1)
        {
            this.comp_markers += this.pot;
        }
        else
        {
            this.comp_markers += this.pot/2;
            this.player_markers += this.pot/2;
        }
    }
    
    public void comp_fold()
    {
        this.player_markers += this.pot;
        this.hand_is_over = true;
        this.street_is_over = true;
    }
    
    public void comp_call()
    {
        int markers = this.player_in_pot-this.comp_in_pot;
        if(markers > this.comp_markers)
        {
            markers = this.comp_markers;
            double player_diff = this.player_in_pot-markers;
            this.player_in_pot -= player_diff;
            this.player_markers += player_diff;        
            this.all_in = true;
        }
        this.comp_in_pot += markers;
        this.comp_markers -= markers;
        this.pot = this.player_in_pot + this.comp_in_pot;
        
        if(this.player_markers == 0)
        {
            this.all_in = true;
        }
        
        
        this.action_count++;
        if(this.action_count > 1 )
        {
            this.street_is_over = true;
        }
        
        set_next_player();
    }
    
    public void comp_check()
    {
        this.action_count++;
        if(this.action_count > 1)
        {
            this.street_is_over = true;
        }       
        set_next_player();
    }
    
    public void comp_bet(int markers)
    {
        if(markers > this.comp_markers)
        {
            markers = this.comp_markers;
        }
        this.comp_in_pot += markers;
        this.comp_markers -= markers;
        this.pot += markers;
    
        this.action_count++;
        set_next_player();
    }
    
    public void player_fold()
    {
        this.comp_markers += this.pot;
        this.street_is_over = true;
        this.hand_is_over = true;
    }
    
    public void player_call()
    {
        
        int markers = this.comp_in_pot - this.player_in_pot;
        if(markers > this.player_markers)
        {
            markers = this.player_markers;
            double player_diff = this.comp_in_pot-markers;
            this.comp_in_pot -= player_diff;
            this.comp_markers += player_diff;
            this.all_in = true;
        }
        this.player_in_pot += markers;
        this.player_markers -= markers;
        this.pot = this.comp_in_pot + this.player_in_pot;
        
        if(this.comp_markers == 0)
        {
            this.all_in = true;
        }
        
        this.action_count++;
        if(this.action_count > 1)
        {
            this.street_is_over = true;
        }
        set_next_player();
    }
    
    public void player_check()
    {
        this.action_count++;
        if(this.action_count > 1)
        {
            this.street_is_over = true;
        }
        set_next_player();
    }
    
    public void player_bet(int markers)
    {
        if(markers > this.player_markers)
        {
            markers = this.player_markers;
        }
        this.player_in_pot += markers;
        this.player_markers -= markers;
        this.pot += markers;
        
        this.action_count++;
        set_next_player();
        
    }
    
    public void adds_blinds()
    {
        if(this.comp_markers > 0 && this.player_markers > 0)
        {
            if(this.dealer == 0)
            {   
                if(this.comp_markers < 10)
                {
                    comp_bet(this.comp_markers);
                    this.all_in = true;
                }
                else
                {
                    comp_bet(10);
                }
                if(this.player_markers < 20)
                {
                    player_bet(this.player_markers);
                    this.all_in = true;
                }
                else
                {
                    player_bet(20);
                }
            }
            else
            {
                if(this.comp_markers < 20)
                {
                    comp_bet(this.comp_markers);
                    this.all_in = true;
                }
                else
                {
                    comp_bet(20);
                }
                if(this.player_markers < 10)
                {
                    player_bet(this.player_markers);
                    this.all_in = true;
                }
                else
                {
                    player_bet(10);
                }
            }
        }
        
        
    }
    
    public void set_next_player()
    {
        this.player_turn++;
        if(this.player_turn % 2 == 0)
        {
            this.player_turn = 0;
        }
        
    }
    
    public void set_init_player_turn()
    {
        
        if(this.dealer == 0)
        {
            
            if(this.street <= 1)
            {
                this.player_turn = 0;
            }
            else if(this.street > 1)
            {
                this.player_turn = 1;
            }
        }
        else
        {
            if(this.street <= 1)
            {
                this.player_turn = 1;
            }
            else if(this.street > 1)
            {
                this.player_turn = 0;
            }
        }
    }
    
    public void next_street()
    {
        
        if(this.player_in_pot == 0 ||this.comp_in_pot == 0)
        {
            this.street_is_over = true;
        }
        if(this.street <= 5)
        {
            this.street_is_over = false;
            this.street++;
            set_init_player_turn();
            this.action_count = 0;
        }
    }
    
    public void new_hand()
    {
        this.player_in_pot = 0;
        this.comp_in_pot = 0;
        this.street_is_over = false;
        this.hand_is_over = false;
        this.all_in = false;
        this.pot = 0;
        this.dealer++;
        if(this.dealer % 2 == 0)
        {
            this.dealer = 0;
        }
        adds_blinds();
        this.street = 0;
        this.action_count = 0;        
    }
    
    public int get_player_markers()
    {
        return this.player_markers;
    }
    
    public int get_comp_markers()
    {
        return this.comp_markers;
    }
    
    public int get_player_in_pot()
    {
        return this.player_in_pot;
    }
    
    public int get_comp_in_pot()
    {
        return this.comp_in_pot;
    }
    
    public int get_pot_size()
    {
        return this.pot;
    }
    
    public int get_dealer()
    {
        return this.dealer;
    }
    
    public boolean is_allin()
    {
        return this.all_in;
    }
}
