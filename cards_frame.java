package cards;

import cards.Card;
import cards.Deck;
import java.awt.Graphics;
import java.awt.Toolkit;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import cards.Engine;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.Timer;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 *
 * @author Henke
 */

public class cards_frame extends javax.swing.JFrame {

    
    final String [] ranking_names = {"High card",
                            "Pair",
                            "Two-pair",
                            "Three of a kind",
                            "Straight",
                            "Flush",
                            "Full house",
                            "Four of a kind",
                            "Straight flush",
                            "Royal Straight flush"
    };
    
    
    
    final int card_width_picture = 66;
    final int card_heigth_picture = 95;
    
    BufferedImage img,sub_image; 
    BufferedImage img_right,img_left;
    BufferedImage dealer,no_dealer;
    
    int street = 0;
    int player_value,opponent_value;
    int manual_hand_street;
    
    private double [] sim_values;
    
    Engine eng;
    private BetHandler b;
    private int delay = 3;
    private Timer t1;
    private int time_delay = 500;
    private int time_delay_offset = 100;
    
    private double AGGRESSION = 0; // 0 - 10
    private double TIGHTNESS = 3; //1 - 3 
    private double SHOW_CARDS_RANGE = 33;   //33%
    private boolean change_style = true;
    private int winner;
    private boolean game_over = false;
    private int x; //computer
    
    
    /**
     * Creates new form cards_frame
     */
    
    
    
    public void computer_bot()
    {
        Random rand = new Random();
        //player has_bet
        
        
        
        boolean no_fold = false;       
        if(eng.get_street() == 2 && eng.eval_flop(1) >= 1)
        {
            //no fold
            no_fold = true;
        }
        if(eng.get_street() == 3 && eng.eval_turn(1) >= 1)
        {
            //no fold
            no_fold = true;
        }
        if(eng.get_street() == 4 && eng.eval_opponent_cards_end() >= 1)
        {
            //no fold
            no_fold = true;
        }
        
        
        if(b.get_comp_in_pot() < b.get_player_in_pot())
        {
            int amount = 0;
            int comp_choice = -1; //0 fold, 1 call, 2 bet
            
            //tightness = 3
            int fold_range = 0;
            int call_range = 0;
            int bet_range = 0;
            
            if(this.TIGHTNESS == 3)
            {
                //tightness = 3
                fold_range = 15;
                call_range = (int)(80-this.AGGRESSION*2);
                bet_range = 100;
            
            }
            else if(this.TIGHTNESS == 2)
            {
                //tightness = 2
                fold_range = 10;
                call_range = (int)(80-this.AGGRESSION*3);
                bet_range = 100;
            }
            else if(this.TIGHTNESS == 1)
            {
                //tightness = 1
                fold_range = 5;
                call_range = (int)(70-this.AGGRESSION*4);
                bet_range = 100;
            }
            
            
            
            if(no_fold)
            {
                fold_range = -1;
                call_range -= 30;
            }
            
            int x = -1;
            if(b.comp_is_bet())
            {
                x = rand.nextInt(101);
            }
            else
            {
                x = rand.nextInt(call_range+1);
            }
           
            
            if(x <= bet_range)
            {
                comp_choice = 2;
                amount = b.get_player_in_pot()*3 - b.get_comp_in_pot();
            }
            if(x <= call_range)
            {
                comp_choice = 1;
            }
            if(x <= fold_range)
            {
                comp_choice = 0;
            }
            
            
            
            if(b.get_comp_markers()*3 < b.get_comp_in_pot())
            {
                // call/bet
                if(this.TIGHTNESS == 3)
                {
                    call_range = 80;
                    bet_range = 100;
            
                }
                else if(this.TIGHTNESS == 2)
                {
                    call_range = 70;
                    bet_range = 100;
                }   
                else if(this.TIGHTNESS == 1)
                {
                    call_range = 60;
                    bet_range = 100;
                }
                
                x = -1;
                if(b.comp_is_bet())
                {
                    x = rand.nextInt(101);
                }
                else
                {
                    x = rand.nextInt(call_range+1);
                }
                
                if(x <= bet_range)
                {
                    comp_choice = 2;
                    amount = b.get_player_in_pot()*3 - b.get_comp_in_pot();
                }
                if(x <= call_range)
                {
                    comp_choice = 1;
                }
                
                
            }
            if(b.get_street() <= 1 && eng.no_fold_pre_flop())
            {
                // call/bet
                if(this.TIGHTNESS == 3)
                {
                    call_range = 50-(int)(this.AGGRESSION);
                    bet_range = 100;
            
                }
                else if(this.TIGHTNESS == 2)
                {
                    call_range = 45-(int)(this.AGGRESSION*2);
                    bet_range = 100;
                }   
                else if(this.TIGHTNESS == 1)
                {
                    call_range = 40-(int)(this.AGGRESSION*2);
                    bet_range = 100;
                }
                
                x = -1;
                if(b.comp_is_bet())
                {
                    x = rand.nextInt(101);
                }
                else
                {
                    x = rand.nextInt(call_range+1);
                }
                
                if(x <= bet_range)
                {
                    comp_choice = 2;
                    amount = b.get_player_in_pot()*3 - b.get_comp_in_pot();
                }
                if(x <= call_range)
                {
                    comp_choice = 1;
                }
            }
            
            
            
            
            if(comp_choice == 0 && b.get_player_turn() == 0)
            {
                timeDelay(time_delay);
                b.comp_fold();
                this.jLabel16.setText("computer fold");
                this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
                update_balances_on_screen();
                timeDelay(time_delay+600);
            }
            else if(comp_choice == 1 && b.get_player_turn() == 0)
            {
                timeDelay(time_delay);
                b.comp_call();
                this.jLabel16.setText("computer call");
                this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
                update_balances_on_screen();
                timeDelay(time_delay);
            }
            else if(comp_choice == 2 && b.get_player_turn() == 0)
            {
                timeDelay(time_delay);
                if(amount > b.get_comp_markers())
                {
                    amount = b.get_comp_markers();
                }
                b.comp_bet(amount);
                this.jLabel16.setText("computer bet "+amount);
                this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
                update_balances_on_screen();
                timeDelay(time_delay);
            }
        }
        if(b.is_check())
        {
            int amount = 0;
            int comp_choice = -1; //0 check, 1 bet
            
            int check_range = 0;
            int bet_range = 0;
            
            if(this.TIGHTNESS == 3)
            {
                //tightness = 3
                check_range = 60 - (int)(this.AGGRESSION);
                bet_range = 100;
            
            }
            else if(this.TIGHTNESS == 2)
            {
                //tightness = 2
                check_range = 55 - (int)(this.AGGRESSION*2);
                bet_range = 100;
            }
            else if(this.TIGHTNESS == 1)
            {
                //tightness = 1
                check_range = 50 - (int)(this.AGGRESSION*3);
                bet_range = 100;
            }
            
            int x = -1;
            if(b.comp_is_bet())
            {
                x = rand.nextInt(101);
            }
            else
            {
                x = rand.nextInt(check_range+1);
            }
            
            if(x <= bet_range)
            {
                comp_choice = 1;
                amount = b.get_player_in_pot()*3 - b.get_comp_in_pot();
            }
            if(x <= check_range)
            {
                comp_choice = 0;
            }
            
           
            
            if(b.get_street() >= 1 && eng.no_fold_pre_flop())
            {
                //check/bet
                if(this.TIGHTNESS == 3)
                {
                    check_range = 50 - (int)(this.AGGRESSION);
                    bet_range = 100;
                }
                else if(this.TIGHTNESS == 2)
                {
                    check_range = 45 - (int)(this.AGGRESSION*2) ;
                    bet_range = 100;
                }
                else if(this.TIGHTNESS == 1)
                {
                    check_range = 35 - (int)(this.AGGRESSION*2);
                    bet_range = 100;
                }
                
                if(no_fold)
                {
                    check_range -= 10;
                }
                
                x = -1;
                if(b.comp_is_bet())
                {
                    x = rand.nextInt(101);
                }
                else
                {
                    x = rand.nextInt(check_range+1);
                }
                
                if(x <= bet_range)
                {
                    comp_choice = 1;
                    amount = b.get_player_in_pot()*3 - b.get_comp_in_pot();
                }
                if(x <= check_range)
                {
                    comp_choice = 0;
                }
                
            }
            
            
            if(comp_choice == 0 && b.get_player_turn() == 0)
            {
                timeDelay(time_delay);
                b.comp_check();
                this.jLabel16.setText("computer check ");
                this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
                update_balances_on_screen();
                timeDelay(time_delay);
            }
            else if(comp_choice == 1 && b.get_player_turn() == 0)
            {
                timeDelay(time_delay);
                if(amount > b.get_comp_markers())
                {
                    amount = b.get_comp_markers();
                }
                b.comp_bet(amount);
                this.jLabel16.setText("computer bet "+amount);
                this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
                update_balances_on_screen();
                timeDelay(time_delay);
            }
        
        }
        
        
        
    }
    
    
    public void set_right()
    {
        
        ImageIcon i_icon = new ImageIcon(img_right);
        this.jLabel15.setIcon(i_icon);
        this.jLabel15.update(this.jLabel15.getGraphics());
    }
    
    public void set_left()
    {
        ImageIcon i_icon = new ImageIcon(img_left);
        this.jLabel15.setIcon(i_icon);
        this.jLabel15.update(this.jLabel15.getGraphics());
    }
    
    public void reset_images()
    {
        ImageIcon i_c = new ImageIcon();
        
        //back side of card
        sub_image = img.getSubimage(0,card_heigth_picture*4, card_width_picture , card_heigth_picture);
        ImageIcon i_icon = new ImageIcon(sub_image);
        
        this.jLabel1.setIcon(null);
        this.jLabel2.setIcon(null);
               
        this.jLabel3.setIcon(null);
        this.jLabel4.setIcon(null);
        this.jLabel5.setIcon(null);
        this.jLabel6.setIcon(null);
        this.jLabel7.setIcon(null);
        this.jLabel3.setText("");
        this.jLabel4.setText("");
        this.jLabel5.setText("");
        this.jLabel6.setText("");
        this.jLabel7.setText("");
        
        this.jLabel9.setIcon(null);
        this.jLabel10.setIcon(null);
        this.jLabel11.setIcon(null);
        
        this.jLabel8.setText("");
        this.jLabel11.setText("");
        this.jLabel12.setText("");
        this.jLabel8.paintImmediately(this.jLabel8.getVisibleRect());
        this.jLabel11.paintImmediately(this.jLabel11.getVisibleRect());
        this.jLabel12.paintImmediately(this.jLabel12.getVisibleRect());
        
        this.jLabel1.update(this.jLabel1.getGraphics());
        this.jLabel2.update(this.jLabel2.getGraphics());
        this.jLabel3.update(this.jLabel3.getGraphics());
        this.jLabel4.update(this.jLabel4.getGraphics());
        this.jLabel5.update(this.jLabel5.getGraphics());
        this.jLabel6.update(this.jLabel6.getGraphics());
        this.jLabel7.update(this.jLabel7.getGraphics());
        this.jLabel9.update(this.jLabel9.getGraphics());
        this.jLabel10.update(this.jLabel10.getGraphics());
        this.jLabel8.update(this.jLabel8.getGraphics());
        this.jLabel11.update(this.jLabel11.getGraphics());
        
        this.jLabel16.setText("");
    }
    
    public void update_player_card_images()
    {
        int val0,val1,suit0,suit1;
        val0 = eng.get_player_card_value(0);
        val1 = eng.get_player_card_value(1);
        suit0 = eng.get_player_card_suit(0);
        suit1 = eng.get_player_card_suit(1);
        
        
        // player cards
        sub_image = img.getSubimage(card_width_picture*val0,card_heigth_picture*suit0, card_width_picture , card_heigth_picture);
        ImageIcon i_icon = new ImageIcon(sub_image);
        this.jLabel1.setIcon(i_icon);
        this.jLabel1.update(this.jLabel1.getGraphics());
        
        sub_image = img.getSubimage(card_width_picture*val1,card_heigth_picture*suit1, card_width_picture , card_heigth_picture);
        i_icon = new ImageIcon(sub_image);
        this.jLabel2.setIcon(i_icon);
        this.jLabel2.update(this.jLabel2.getGraphics());
    }
    
    public void update_opponent_card_images(int show_cards)
    {
        if(show_cards == 0)
        {
            int val0,val1,suit0,suit1;        
            val0 = eng.get_opp_card_value(0);
            val1 = eng.get_opp_card_value(1);
            suit0 = eng.get_opp_card_suit(0);
            suit1 = eng.get_opp_card_suit(1);
            
            sub_image = img.getSubimage(card_width_picture*val0,card_heigth_picture*suit0, card_width_picture , card_heigth_picture);
            ImageIcon  i_icon = new ImageIcon(sub_image);
            this.jLabel9.setIcon(i_icon);
            this.jLabel9.update(this.jLabel9.getGraphics());
        
            sub_image = img.getSubimage(card_width_picture*val1,card_heigth_picture*suit1, card_width_picture , card_heigth_picture);
            i_icon = new ImageIcon(sub_image);
            this.jLabel10.setIcon(i_icon);
            this.jLabel10.update(this.jLabel10.getGraphics());
            
            
        }
        else
        {
            sub_image = img.getSubimage(0,card_heigth_picture*4, card_width_picture , card_heigth_picture);
            ImageIcon i_icon = new ImageIcon(sub_image);
            this.jLabel9.setIcon(i_icon);
            this.jLabel10.setIcon(i_icon);
        }
        
        
    }
    
    public void update_board_images(int n)
    {
        ImageIcon i_icon;
        int val = 0,suit = 0;
       
        
        switch(n)
        {
            case 0:
                //preflop
                update_player_card_images();
                update_opponent_card_images(1);
                break;
            case 1:
                // flop
                
                val = eng.get_board_value(0);
                suit = eng.get_board_suit(0);
                sub_image = img.getSubimage(card_width_picture*val,card_heigth_picture*suit, card_width_picture , card_heigth_picture);
                i_icon = new ImageIcon(sub_image);
                this.jLabel3.setIcon(i_icon);
                
                val = eng.get_board_value(1);
                suit = eng.get_board_suit(1);
                sub_image = img.getSubimage(card_width_picture*val,card_heigth_picture*suit, card_width_picture , card_heigth_picture);
                i_icon = new ImageIcon(sub_image);
                this.jLabel4.setIcon(i_icon);
                
                val = eng.get_board_value(2);
                suit = eng.get_board_suit(2);
                sub_image = img.getSubimage(card_width_picture*val,card_heigth_picture*suit, card_width_picture , card_heigth_picture);
                i_icon = new ImageIcon(sub_image);
                this.jLabel5.setIcon(i_icon);
                break;
            case 2:
                // turn
                val = eng.get_board_value(3);
                suit = eng.get_board_suit(3);
                 sub_image = img.getSubimage(card_width_picture*val,card_heigth_picture*suit, card_width_picture , card_heigth_picture);
                i_icon = new ImageIcon(sub_image);
                this.jLabel6.setIcon(i_icon);
                break;
            case 3:
                val = eng.get_board_value(4);
                suit = eng.get_board_suit(4);
                sub_image = img.getSubimage(card_width_picture*val,card_heigth_picture*suit, card_width_picture , card_heigth_picture);
                i_icon = new ImageIcon(sub_image);
                this.jLabel7.setIcon(i_icon);
                //river
                break;
            case 4:
                break;
            default:
                break;
        }
    }
    
    public void update_player_non_choices()
    {
        
        
        this.jButton9.setVisible(false);
        this.jButton10.setVisible(false);
        this.jButton11.setVisible(false);
        this.jButton12.setVisible(false);
        this.jButton13.setVisible(false);
        this.jTextField1.setVisible(false);
        this.jSlider1.setVisible(false);
        
        
        this.jButton9.paintImmediately(this.jButton9.getVisibleRect());
        this.jButton10.paintImmediately(this.jButton9.getVisibleRect());
        this.jButton11.paintImmediately(this.jButton9.getVisibleRect());
        this.jButton12.paintImmediately(this.jButton9.getVisibleRect());
        this.jButton13.paintImmediately(this.jButton9.getVisibleRect());
        this.jTextField1.paintImmediately(this.jTextField1.getVisibleRect());
        
    }
    
    public void update_player_choices()
    {
        
        //always possible fold
        this.jButton9.setVisible(true);
        if(b.player_is_call())
        {
            this.jButton10.setVisible(true);
        }
        else
        {
            this.jButton10.setVisible(false);
        }
        if(b.player_is_bet())
        {
            this.jTextField1.setVisible(true);
            this.jSlider1.setVisible(true);
            this.jSlider1.setMinimum(b.get_minimum_player_bet());
            this.jSlider1.setMaximum(b.get_maximum_player_bet());
            this.jButton11.setVisible(true);
            this.jButton13.setVisible(true);
        }
        else
        {
            this.jTextField1.setVisible(false);
            this.jSlider1.setVisible(false);
            this.jButton11.setVisible(false);
            this.jButton13.setVisible(false);
        }
        if(b.is_check())
        {
            this.jButton12.setVisible(true);
        }
        else
        {
            this.jButton12.setVisible(false);
        }
    }
    
    
    

public void act_perf(int delay)
    {
        ActionListener taskPerformer = new ActionListener()
                {
                    
                    public void actionPerformed(ActionEvent evt)
                    {
                        if(!game_over)
                        {
                            update();
                        }
                        else
                        {
                            t1.stop();
                            
                        }
                    }
                };
        
        
        
        t1 = new Timer(delay,taskPerformer);
        t1.start();
    }

public void clean_screen_game_over()
{
    this.jLabel1.setIcon(null);
    this.jLabel2.setIcon(null);
    this.jLabel9.setIcon(null);
    this.jLabel10.setIcon(null);
    this.jLabel15.setIcon(null);
    this.jLabel17.setIcon(null);
    this.jLabel18.setIcon(null);
    this.jLabel1.setText("");
    this.jLabel2.setText("");
    this.jLabel9.setText("");
    this.jLabel10.setText("");
    this.jLabel15.setText("");
    this.jLabel17.setText("");
    this.jLabel18.setText("");
    this.jLabel8.setText("");
    this.jLabel11.setText("");
    this.jLabel12.setText("");
    this.jLabel13.setText("");
    this.jLabel14.setText("");
    this.jTextField1.setVisible(false);
    this.jSlider1.setVisible(false);
    this.jButton9.setVisible(false);
    this.jButton10.setVisible(false);
    this.jButton11.setVisible(false);
    this.jButton12.setVisible(false);
    this.jButton13.setVisible(false);
    
    
    //this.jLabel13.setText("");
}

public boolean game_is_over()
{
    boolean status = false;
    if(b.get_player_markers() <= 0 && b.get_player_in_pot() <= 0)
    {
        status = true;
    }
    if(b.get_comp_markers() <= 0 && b.get_comp_in_pot() <= 0)
    {
        status = true;
    }
    return status;
}

public void update()
{
    
    
    if(b.is_allin())
    {
        this.jLabel15.setIcon(null);
        this.jLabel17.setIcon(null);
        this.jLabel18.setIcon(null);
        this.jLabel15.setText("");
        this.jLabel17.setText("");
        this.jLabel18.setText("");
        timeDelay(400);
        update_opponent_card_images(0);
        next_street();
        timeDelay(400);
        
    }
    else if(!b.hand_is_over() && b.get_street() <= 5)
    {
        if(b.get_player_turn() == 0 && !b.street_is_over())
        {
            update_player_non_choices();
            set_left();      
            computer_bot();
        }
        else if(b.get_player_turn() == 1 && !b.street_is_over())
        {
            set_right();
            update_player_choices();
        }
        if(b.street_is_over() && !b.hand_is_over() && !game_over)
        {
            next_street();
        }
    }
    else if(b.hand_is_over() && !b.is_allin() && !game_over)   //hand is over
    {
        new_deal();
    }
    
    if(b.get_player_markers() <= 0 && b.get_player_in_pot() <= 0)
    {
        game_over = true;
        winner = 0;
        this.jLabel16.setText("Computer wins ");
        this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
        clean_screen_game_over();
    }
    if(b.get_comp_markers() <= 0 && b.get_comp_in_pot() <= 0)
    {
        game_over = true;
        winner = 1;
        this.jLabel16.setText("Player wins ");
        this.jLabel16.paintImmediately(this.jLabel16.getVisibleRect());
        clean_screen_game_over();
    }
    
}

public void timeDelay(long t) 
    {
    try {
        Thread.sleep(t);
    } catch (InterruptedException e) 
    {}
}

    public cards_frame() throws IOException {
        
        initComponents();
        Container c = this.getContentPane();
        c.setBackground(Color.white);
        
        super.setLayout(null);
        
        img = ImageIO.read(new File("img_cards.png"));
        img_right = ImageIO.read(new File("right.png"));
        img_left = ImageIO.read(new File("left.png"));
        dealer = ImageIO.read(new File("dealer.png"));
        no_dealer = ImageIO.read(new File("no_dealer.png"));
        
        game_over = false;
        eng = new Engine();
        b = new BetHandler();
        init_computer_style();
        new_deal();
        act_perf(delay);
        
    }
    
    public void init_computer_style()
    {
        Random rand = new Random();
        this.AGGRESSION = 1 + rand.nextInt(10);
        this.TIGHTNESS = 1 +rand.nextInt(3);
    }
    
    public void new_deal()
    {
        if(change_style)
        {
            init_computer_style();
        }
        
        
        if(b.get_player_markers() >= 60)
        {
            jSlider1.setValue(60);
        }
        else
        {
            jSlider1.setValue(b.get_player_markers());
        }
        
        
        reset_images();
        eng.new_hand();
        b.new_hand();
        if(!game_is_over())
        {
            next_street();
            if(b.get_dealer() == 0)
            {
                left_dealer();
            }
            else
            {
                right_dealer();
            }
        }
        
    }
    
   
    public void right_dealer()
    {
        ImageIcon i_icon = new ImageIcon(dealer);
        this.jLabel17.setIcon(i_icon);
        this.jLabel17.update(this.jLabel17.getGraphics());
        i_icon = new ImageIcon(no_dealer);
        this.jLabel18.setIcon(i_icon);
        this.jLabel18.update(this.jLabel18.getGraphics());
    }
    
    public void left_dealer()
    {
        ImageIcon i_icon = new ImageIcon(no_dealer);
        this.jLabel17.setIcon(i_icon);
        this.jLabel17.update(this.jLabel17.getGraphics());
        i_icon = new ImageIcon(dealer);
        this.jLabel18.setIcon(i_icon);
        this.jLabel18.update(this.jLabel18.getGraphics());
    }
    
    public void update_balances_on_screen()
    {
        this.jLabel12.setText("Pot "+b.get_pot_size());
        this.jLabel13.setText(String.valueOf(b.get_player_markers()));
        this.jLabel14.setText(String.valueOf(b.get_comp_markers()));
        this.jLabel8.setText(String.valueOf(b.get_player_in_pot()));
        this.jLabel11.setText(String.valueOf(b.get_comp_in_pot()));
        
        //opponent value > 1 => no fold
        if(eng.get_street() >= 2 && eng.get_street() <= 5)
        {
            opponent_value = 0;
            if(eng.get_street() == 2)
            {
                opponent_value = eng.eval_flop(1);
            }
            else if(eng.get_street() == 3)
            {
                opponent_value = eng.eval_turn(1);
            }
            else if(eng.get_street() == 5)
            {
                
                update_opponent_card_images(0);
                player_value = eng.eval_player_cards_end();
                opponent_value = eng.eval_opponent_cards_end();
                int winner = eng.get_river_winner(); //player : 0, comp: 1 draw:-1
                b.add_eval_end_pot(winner);
                //timeDelay(1500);
                if(winner == 0)
                {
                    this.jLabel12.setText(ranking_names[player_value]+" player "+b.get_pot_size());
                }
                else if(winner == 1)
                {
                    this.jLabel12.setText(ranking_names[opponent_value]+" computer "+b.get_pot_size());
                }
                else
                {
                    this.jLabel12.setText(ranking_names[player_value]+" draw "+b.get_pot_size());
                }
                this.jLabel12.paintImmediately(this.jLabel12.getVisibleRect());
                if(eng.get_street() >= 5)
                {
                    this.jLabel8.setText("");
                    this.jLabel11.setText("");
                    this.jLabel8.paintImmediately(this.jLabel8.getVisibleRect());
                    this.jLabel11.paintImmediately(this.jLabel8.getVisibleRect());
                    this.jLabel8.setText(ranking_names[player_value]);
                    this.jLabel11.setText(ranking_names[opponent_value]);
            
                }
                
                timeDelay(4000);
                new_deal();
                
            }
        }
        
        
        this.jLabel8.update(this.jLabel8.getGraphics());
        this.jLabel11.update(this.jLabel11.getGraphics());
        this.jLabel3.update(this.jLabel3.getGraphics());
        this.jLabel4.update(this.jLabel4.getGraphics());
        this.jLabel5.update(this.jLabel5.getGraphics());
        this.jLabel6.update(this.jLabel6.getGraphics());
        this.jLabel7.update(this.jLabel7.getGraphics());
    }
    
    public void simulate_win(int street)
    {
        //values 0 = player_perc
        //values 1 = opponent_perc
        //values 2 = draw_perc
        
        //street == 0 preflop
        //       == 1 flop
        //       == 2 turn
        
        sim_values = new double [3];
        for(int z = 0; z < 3; z++)
        {
            sim_values[z] = 0.0;
        }
        eng.simulate_winner(street, sim_values);
        for(int z = 0; z < 3; z++)
        {
            // (x/1000)/10 = %
            sim_values[z] /= 10;
        }
        
        String s = "";
        s += "Player: "+String.valueOf(sim_values[0]+ "%");
        s += "\n";
        s += "Opponent: "+String.valueOf(sim_values[1]+ "%");
        s += "\n";
        s += "Draw: "+String.valueOf(sim_values[2]+ "%");
        s += "\n";
        this.jTextArea1.setText(s);
        this.jTextArea1.paintImmediately(this.jTextArea1.getVisibleRect());
        
        
        
    }
    
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jCheckBox2 = new javax.swing.JCheckBox();

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextArea1.setFocusable(false);
        jScrollPane1.setViewportView(jTextArea1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(255, 255, 255));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });

        jButton1.setText("New game");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("jLabel1");
        jLabel1.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel2.setText("jLabel1");
        jLabel2.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel3.setText("jLabel1");
        jLabel3.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel4.setText("jLabel1");
        jLabel4.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel5.setText("jLabel1");
        jLabel5.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel6.setText("jLabel1");
        jLabel6.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel7.setText("jLabel1");
        jLabel7.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("...");

        jLabel9.setText("jLabel1");
        jLabel9.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel10.setText("jLabel1");
        jLabel10.setMaximumSize(new java.awt.Dimension(135, 189));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 15)); // NOI18N
        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("...");

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("...");

        jLabel13.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel13.setText("Player");

        jLabel14.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel14.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel14.setText("Opponent");

        jLabel15.setText("jLabel15");

        jButton9.setText("fold");
        jButton9.setMaximumSize(new java.awt.Dimension(65, 30));
        jButton9.setPreferredSize(new java.awt.Dimension(65, 30));
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setText("call");
        jButton10.setPreferredSize(new java.awt.Dimension(65, 30));
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("raise");
        jButton11.setPreferredSize(new java.awt.Dimension(65, 30));
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("check");
        jButton12.setPreferredSize(new java.awt.Dimension(65, 30));
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("raise x3");
        jButton13.setPreferredSize(new java.awt.Dimension(65, 30));
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jSlider1.setMajorTickSpacing(1);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setAutoscrolls(true);
        jSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSlider1StateChanged(evt);
            }
        });

        jLabel17.setText("jLabel17");
        jLabel17.setPreferredSize(new java.awt.Dimension(50, 50));

        jLabel18.setText("jLabel17");
        jLabel18.setPreferredSize(new java.awt.Dimension(50, 50));

        jTextField1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jTextField1.setText("0");

        jLabel16.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("...");

        jCheckBox2.setSelected(true);
        jCheckBox2.setText("changing tactic");
        jCheckBox2.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jCheckBox2StateChanged(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(324, 324, 324)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jCheckBox2)
                .addGap(138, 138, 138))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(244, 244, 244)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(57, 57, 57))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(65, 65, 65)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(85, 85, 85)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 68, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(283, 283, 283))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox2)
                    .addComponent(jButton1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(82, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(27, 27, 27)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(48, 48, 48)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(5, 5, 5)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(230, 230, 230))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    
    
    public void next_street()
    {
        eng.next_street();
        b.next_street();
        jSlider1.setValue(jSlider1.getMinimum());
        update_board_images(eng.get_street()-1);
        update_balances_on_screen();
    }
   
    
    
            
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

        game_over = false;
        eng = new Engine();
        b = new BetHandler();
        init_computer_style();
        new_deal();
        act_perf(delay);
        update_player_choices();
// TODO add your handling code here:
        
        
        
    }//GEN-LAST:event_jButton1ActionPerformed
    
    private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
        

// TODO add your handling code here:
    }//GEN-LAST:event_formMouseClicked

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

        
        // TODO add your handling code here:
    }//GEN-LAST:event_formKeyPressed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed

        b.player_fold();
        update_player_non_choices();
        this.jLabel16.setText("Player fold");
        update_balances_on_screen();
        Random rand = new Random();
        int x =  rand.nextInt(101);
        if(x < (int)(SHOW_CARDS_RANGE+this.AGGRESSION*2))
        {
            timeDelay(500);
            update_opponent_card_images(0);
            update_balances_on_screen();
            timeDelay(1000);
        }
        
        
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed

        b.player_call();
        update_player_non_choices();
        this.jLabel16.setText("Player call");
        update_balances_on_screen();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed

        int value = Integer.parseInt(this.jTextField1.getText());
        /*
        if(value > b.get_minimum_player_bet())
        {
            b.player_bet(value);
        }
        */
        /*
        if(value > b.get_player_markers())
        {
            value = b.get_player_markers();
        }
        */
        b.player_bet(value);
        update_player_non_choices();
        this.jLabel16.setText("Player bet "+value);
        update_balances_on_screen();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jSlider1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSlider1StateChanged

        
        this.jTextField1.setText(String.valueOf(this.jSlider1.getValue()));
        update_balances_on_screen();
        // TODO add your handling code here:
    }//GEN-LAST:event_jSlider1StateChanged

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed

        int amount = b.get_comp_in_pot()*3 - b.get_player_in_pot();
        b.player_bet(amount);
        update_player_non_choices();
        this.jLabel16.setText("Player bet "+amount);
        update_balances_on_screen();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed

        b.player_check();
        update_player_non_choices();
        this.jLabel16.setText("Player check ");
        update_balances_on_screen();
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jCheckBox2StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jCheckBox2StateChanged

        if(this.jCheckBox2.isSelected())
        {
            this.change_style = true;
        }
        else
        {
            this.change_style = false;
        }
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox2StateChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(cards_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cards_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cards_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cards_frame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new cards_frame().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(cards_frame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton9;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
