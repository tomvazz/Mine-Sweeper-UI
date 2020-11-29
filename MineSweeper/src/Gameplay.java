import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.util.ArrayList;

public class Gameplay implements KeyListener{

	public JButton[][] block = new JButton[16][16];
	public JLabel[][] blocklbl = new JLabel[16][16];
	public JLabel[][] flags = new JLabel[16][16];
	public String[][] pos = new String[16][16];
	public String[][] numsandbombs = new String [16][16];
	public JLabel countflag;
	public JLabel winner1;
	public JLabel winner2;
	public JLabel flagicon;
	public boolean firstmove = true;
	public int gameover = 0;
	public int flagcount = 40;
	
	public ImageIcon lpurple;
	public ImageIcon dpurple;
	public ImageIcon ltan;
	public ImageIcon dtan;
	public ImageIcon red;
	
	//timer
	public JLabel timerones;
	public JLabel timertens;
	public JLabel colon;
	public JLabel timerminutes;
	public int onespassed = 0;
	public int tenspassed = 0;
	public int minutespassed = 0;
	Timer t = new Timer();
	TimerTask tt = new TimerTask() {
		@Override
		public void run() {
			if (gameover == 0) {
				onespassed++;
				if (onespassed == 10) {
					onespassed = 0;
					tenspassed++;
					if (tenspassed == 6) {
						tenspassed = 0;
						minutespassed++;
						timerminutes.setText(String.valueOf(minutespassed));
					}
					timertens.setText(String.valueOf(tenspassed));
				}
				timerones.setText(String.valueOf(onespassed));
			}
		}
	};
	
	
	//AI Stuff
	public double[][] aiscore = new double[16][16];
	public String[][] aiflagged = new String[16][16];
	public int millisecondtimer = 0; // timer must be set at 0,1 (speed set at 1)
	public int speed = 100; // change this to change speed of moves
	Timer comp = new Timer();
	TimerTask comptask = new TimerTask() {
		public void run() {
			if (firstmove) {
				move1();
				firstmove = false;
			} else {
				flagger();
				opensquares();
				move();
			}
			millisecondtimer++;
		}
	};
	
	public void play(JFrame f) {
		
		f.addKeyListener(this);
		f.setFocusable(true);
		
		lpurple = new ImageIcon(this.getClass().getResource("/lpurple.png"));
		dpurple = new ImageIcon(this.getClass().getResource("/dpurple.png"));
		ltan = new ImageIcon(this.getClass().getResource("/ltan.png"));
		dtan = new ImageIcon(this.getClass().getResource("/dtan.png"));
		red = new ImageIcon(this.getClass().getResource("/red.png"));
		
		//timerlabels
		timerones = new JLabel(String.valueOf(onespassed));
		timerones.setFont(new Font("Futura", Font.PLAIN, 43));
		timerones.setHorizontalAlignment(SwingConstants.CENTER);
		timerones.setForeground(Color.WHITE);
		timerones.setBounds(202, 575, 45, 50);
		f.getContentPane().add(timerones);
		timertens = new JLabel(String.valueOf(tenspassed));
		timertens.setFont(new Font("Futura", Font.PLAIN, 43));
		timertens.setHorizontalAlignment(SwingConstants.CENTER);
		timertens.setForeground(Color.WHITE);
		timertens.setBounds(175, 575, 45, 50);
		f.getContentPane().add(timertens);
		colon = new JLabel(String.valueOf(":"));
		colon.setFont(new Font("Futura", Font.PLAIN, 43));
		colon.setHorizontalAlignment(SwingConstants.CENTER);
		colon.setForeground(Color.WHITE);
		colon.setBounds(155, 570, 40, 50);
		f.getContentPane().add(colon);
		timerminutes = new JLabel(String.valueOf(minutespassed));
		timerminutes.setFont(new Font("Futura", Font.PLAIN, 43));
		timerminutes.setHorizontalAlignment(SwingConstants.RIGHT);
		timerminutes.setForeground(Color.WHITE);
		timerminutes.setBounds(108, 575, 60, 50);
		f.getContentPane().add(timerminutes);
		
		t.schedule(tt, 1000, 1000);

		flagicon = new JLabel("O");
		flagicon.setBackground(new Color(255, 102, 102));
		flagicon.setForeground(new Color(255, 51, 102));
		flagicon.setFont(new Font("Wingdings", Font.BOLD, 59));
		flagicon.setHorizontalAlignment(SwingConstants.CENTER);
		flagicon.setBounds(255, 567, 60, 60);
		f.getContentPane().add(flagicon);
		
		countflag = new JLabel(String.valueOf(flagcount));
		countflag.setFont(new Font("Futura", Font.PLAIN, 43));
		countflag.setHorizontalAlignment(SwingConstants.CENTER);
		countflag.setForeground(Color.WHITE);
		countflag.setBounds(322, 575, 70, 50);
		f.getContentPane().add(countflag);
		
		winner1 = new JLabel(" ");
		winner1.setForeground(new Color(102, 204, 51));
		winner1.setFont(new Font("Futura", Font.PLAIN, 20));
		winner1.setHorizontalAlignment(SwingConstants.CENTER);
		winner1.setBounds(20, 577, 100, 50);
		f.getContentPane().add(winner1);
		winner2 = new JLabel(" ");
		winner2.setForeground(new Color(102, 204, 51));
		winner2.setFont(new Font("Futura", Font.PLAIN, 20));
		winner2.setHorizontalAlignment(SwingConstants.CENTER);
		winner2.setBounds(430, 577, 100, 50);
		f.getContentPane().add(winner2);
		
		//array set up
        for (int i = 0; i < 16; i++){
            for (int e = 0; e < 16; e++){
                pos[i][e] = ".";
            }
        }
        for (int i = 0; i < 16; i++){
            for (int e = 0; e < 16; e++){
                numsandbombs[i][e] = " ";
            }
        } 
        
        for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				flags[a][b] = new JLabel(" ");
				flags[a][b].setBounds((a*35), (525 - (b*35)), 35, 35);
				flags[a][b].setHorizontalAlignment(SwingConstants.CENTER);
				f.getContentPane().add(flags[a][b]);
			}
		}
        
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				blocklbl[a][b] = new JLabel(" ");
				blocklbl[a][b].setBounds((a*35), (525 - (b*35)), 35, 35);
				blocklbl[a][b].setFont(new Font("Futura", Font.BOLD, 25));
				blocklbl[a][b].setHorizontalAlignment(SwingConstants.CENTER);
				f.getContentPane().add(blocklbl[a][b]);
			}
		}
		
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (((a+b) % 2) == 0) {
					block[a][b] = new JButton(dpurple);
				} else {
					block[a][b] = new JButton(lpurple);
				}
				block[a][b].setBounds((a*35), (525 - (b*35)), 35, 35);	
				f.getContentPane().add(block[a][b]);
			}
		}
		
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				int x = a;
				int y = b;
				block[a][b].addMouseListener(new MouseListener() {
					public void mouseClicked(MouseEvent e) {}
					public void mousePressed(MouseEvent e) {
						if (gameover == 0) {
							if (SwingUtilities.isLeftMouseButton(e)) {
								if (firstmove) {
									setup(x, y);
									setupnums();
									emptyspace();
									execute();
									firstmove = false;
								} else {
									if (numsandbombs[x][y] == "X") {
										bombhit();
										execute();
									} else {
										pos[x][y] = numsandbombs[x][y];
										if (flags[x][y].getText().equals("O")) {
											flagcount++;
											countflag.setText(String.valueOf(flagcount));
											flags[x][y].setText(" ");
										}
										emptyspace();
										execute();
									}
								}
							} else if (SwingUtilities.isRightMouseButton(e)) {
								if (pos[x][y] == ".") {
									if (flags[x][y].getText() == "O") {
										flags[x][y].setText(" ");
										flagcount++;
										countflag.setText(String.valueOf(flagcount));
									} else {
										flags[x][y].setText("O");
										flags[x][y].setForeground(new Color(220, 20, 60));
										flags[x][y].setFont(new Font("Wingdings", Font.BOLD, 29));
										flagcount--;
										countflag.setText(String.valueOf(flagcount));
									}
								}
								
								if (flagcount == 0) {
									int accuracycount = 0;
									for (int c = 0; c < 16; c++) {
										for (int d = 0; d < 16; d++) {
											if (numsandbombs[c][d] == "X" && flags[c][d].getText() == "O") {
												accuracycount++;
											}
										}
									}
									if (accuracycount == 40) {
										gameover = 1;
										winner1.setText("winner!");
										winner2.setText("winner!");
										flagicon.setForeground(new Color(102, 204, 51));
									}
								}
								
							}
						}
						
					}
					public void mouseReleased(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
			    });
			}
		}
	}
	
	public void execute() {
		
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (pos[a][b] != "." && pos[a][b] != "F") {
					if (((a+b) % 2) == 0) {
						block[a][b].setIcon(dtan);
					} else {
						block[a][b].setIcon(ltan);
					}
				}	
				if (pos[a][b] == "X") {
					blocklbl[a][b].setText("M");
					blocklbl[a][b].setForeground(Color.BLACK);
					blocklbl[a][b].setFont(new Font("Wingdings", Font.BOLD, 22));
					block[a][b].setIcon(red);
				}
			}
		}
		
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (numsandbombs[a][b] != " " && pos[a][b] != ".") {
					if (numsandbombs[a][b].equals("1")) {
						blocklbl[a][b].setText("1");
						blocklbl[a][b].setForeground(new Color(64, 224, 208));
					} else if (numsandbombs[a][b].equals("2")) {
						blocklbl[a][b].setText("2");
						blocklbl[a][b].setForeground(new Color(186, 85, 211));
					} else if (numsandbombs[a][b].equals("3")) {
						blocklbl[a][b].setText("3");
						blocklbl[a][b].setForeground(Color.RED);
					} else if (numsandbombs[a][b].equals("4")) {
						blocklbl[a][b].setText("4");
						blocklbl[a][b].setForeground(new Color(60, 179, 113));
					} else if (numsandbombs[a][b].equals("5")) {
						blocklbl[a][b].setText("5");
						blocklbl[a][b].setForeground(new Color(255, 140, 0));
					} else if (numsandbombs[a][b].equals("6")) {
						blocklbl[a][b].setText("6");
						blocklbl[a][b].setForeground(Color.PINK);
					} else if (numsandbombs[a][b].equals("7")) {
						blocklbl[a][b].setText("7");
						blocklbl[a][b].setForeground(Color.DARK_GRAY);
					} else if (numsandbombs[a][b].equals("8")) {
						blocklbl[a][b].setText("8");
						blocklbl[a][b].setForeground(Color.BLACK);
					}
				}
			}
		}
		
	}
	
	public void setup(int x, int y) {
		pos[x][y] = " ";
		//ensures a bomb isnt touching starting move
        if (y != 15) {
            pos[x][y + 1] = " ";
        }
        if (y != 0) {
            pos[x][y - 1] = " ";
        }
        if (x != 15) {
            pos[x + 1][y] = " ";
        }
        if (x != 0) {
            pos[x - 1][y] = " ";
        }
        if (x != 15 && y != 15) {
            pos[x + 1][y + 1] = " ";
        }
        if (x != 0 && y != 0) {
            pos[x - 1][y - 1] = " ";
        }
        if (x != 0 && y != 15) {
            pos[x - 1][y + 1] = " ";
        }
        if (x != 15 && y != 0) {
            pos[x + 1][y - 1] = " ";
        }
       
        //random placement of 40 bombs
        for (int a = 0; a < 40; a++){
            int whilecount = 0;
            while(whilecount == 0){
                int randx = (int)(Math.random() * 15 + 0);
                int randy = (int)(Math.random() * 15 + 0);
                if (pos[randx][randy] == "." && numsandbombs[randx][randy] != "X"){
                    numsandbombs[randx][randy] = "X";
                    System.out.println(randx + ", " + randy);
                    whilecount = 1;
                }
            }
        }
        
	}
	
	public void setupnums() {
		
		for (int a = 0; a < 16; a++){
            for (int b = 0; b < 16; b++){
                if (numsandbombs[a][b] == " ") {

                    int bombcount = 0;
                    //north
                    if (b != 15) {
                        if (numsandbombs[a][b + 1] == "X") {
                            bombcount++;
                        }
                    }
                    //south
                    if (b != 0) {
                        if (numsandbombs[a][b - 1] == "X") {
                            bombcount++;
                        }
                    }
                    //east
                    if (a != 15) {
                        if (numsandbombs[a + 1][b] == "X") {
                            bombcount++;
                        }
                    }
                    //west
                    if (a != 0) {
                        if (numsandbombs[a - 1][b] == "X") {
                            bombcount++;
                        }
                    }
                    //northeast
                    if ((a != 15) && (b != 15)) {
                        if (numsandbombs[a + 1][b + 1] == "X") {
                            bombcount++;
                        }
                    }
                    //northwest
                    if ((a != 0) && (b != 15)) {
                        if (numsandbombs[a - 1][b + 1] == "X") {
                            bombcount++;
                        }
                    }
                    //southwest
                    if ((a != 0) && (b != 0)) {
                        if (numsandbombs[a - 1][b - 1] == "X") {
                            bombcount++;
                        }
                    }
                    //southeast
                    if ((a != 15) && (b != 0)) {
                        if (numsandbombs[a + 1][b - 1] == "X") {
                            bombcount++;
                        }
                    }


                    if (bombcount > 0) {
                        numsandbombs[a][b] = String.valueOf(bombcount);
                    }


                }
            }
        }
		
		for (int a = 0; a < 9; a++){
            for (int b = 0; b < 9; b++){
                if (pos[a][b] == " " && numsandbombs[a][b] != " "){
                    pos[a][b] = numsandbombs[a][b];
                }
            }
        }
		
	}
	
	public void emptyspace() {
		//repeat 5 times to be safe
        for (int t = 0; t < 5; t++) {
        	
            for (int a = 0; a < 16; a++) {
                for (int b = 0; b < 16; b++) {

                    if (pos[a][b] == " ") {
                        //north
                        for (int y = b; y < 16; y++) {
                            if (numsandbombs[a][y] == " ") {
                                pos[a][y] = " ";
                            } else {
                                pos[a][y] = numsandbombs[a][y];
                                break;
                            }
                        }
                        //south
                        for (int y = b; y >= 0; y--) {
                            if (numsandbombs[a][y] == " ") {
                                pos[a][y] = " ";
                            } else {
                                pos[a][y] = numsandbombs[a][y];
                                break;
                            }
                        }
                        //east
                        for (int y = a; y < 16; y++) {
                            if (numsandbombs[y][b] == " ") {
                                pos[y][b] = " ";
                            } else {
                                pos[y][b] = numsandbombs[y][b];
                                break;
                            }
                        }
                        //west
                        for (int y = a; y >= 0; y--) {
                            if (numsandbombs[y][b] == " ") {
                                pos[y][b] = " ";
                            } else {
                                pos[y][b] = numsandbombs[y][b];
                                break;
                            }
                        }

                        if (a != 15 && b != 15) {
                            if ((pos[a][b + 1] != ".") && (pos[a + 1][b] != ".")) {
                                pos[a + 1][b + 1] = numsandbombs[a + 1][b + 1];
                            }
                        }
                        if (a != 15 && b != 0) {
                            if ((pos[a][b - 1] != ".") && (pos[a + 1][b] != ".")) {
                                pos[a + 1][b - 1] = numsandbombs[a + 1][b - 1];
                            }
                        }
                        if (a != 0 && b != 0) {
                            if ((pos[a - 1][b] != ".") && (pos[a][b - 1] != ".")) {
                                pos[a - 1][b - 1] = numsandbombs[a - 1][b - 1];
                            }
                        }
                        if (a != 0 && b != 15) {
                            if ((pos[a - 1][b] != ".") && (pos[a][b + 1] != ".")) {
                                pos[a - 1][b + 1] = numsandbombs[a - 1][b + 1];
                            }
                        }
                    }

                }
            }


        }
		
	}
	
	public void bombhit() {
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (numsandbombs[a][b] == "X") {
					pos[a][b] = "X";
				}
				flags[a][b].setText(" ");
			}
		}
		
		gameover = 1;
	}

	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int spacecode = e.getKeyCode();
		if (spacecode == KeyEvent.VK_1) {
			start();
		}
	}
	public void keyReleased(KeyEvent e) {}
	
	
	
	/*
	 * 
	 * AI SECTION
	 * 
	 */
	
	//type in "1" to activate AI
	
	
	
	public void start() {
		comp.schedule(comptask, 0, speed); //change to (comptask, 0, 1) to get the fastest time
	}
	
	public void move1() {
		int xrand = (int)(Math.random() * 7 + 4); //pick near center
        int yrand = (int)(Math.random() * 7 + 4); // pick near center
		System.out.println("xrand: " + xrand + ", yrand: " + yrand);
        setup(xrand, yrand);
		setupnums();
		emptyspace();
		execute();
	}
	
	public void aiexecute(int i, int e, int isflag) {
		
		if (isflag == 0) {
			if (numsandbombs[i][e] == "X") {
				//comp.cancel();  // to see if you can complete when the ai hits a bomb
				bombhit();
				execute();
			} else {
				pos[i][e] = numsandbombs[i][e];
				emptyspace();
				execute();
			}
		} else if (isflag == 1) {
			flags[i][e].setText("O");
			flags[i][e].setForeground(new Color(220, 20, 60));
			flags[i][e].setFont(new Font("Wingdings", Font.BOLD, 29));
			flagcount--;
			countflag.setText(String.valueOf(flagcount));
			aiflagged[i][e] = "F";
			
			if (flagcount == 0) {
				int accuracycount = 0;
				for (int c = 0; c < 16; c++) {
					for (int d = 0; d < 16; d++) {
						if (numsandbombs[c][d] == "X" && flags[c][d].getText() == "O") {
							accuracycount++;
						}
					}
				}
				if (accuracycount == 40) {
					if (speed == 1) {
						System.out.println(" ");
						System.out.println(millisecondtimer + " milliseconds");
					}
					gameover = 1;
					winner1.setText("winner!");
					winner2.setText("winner!");
					flagicon.setForeground(new Color(102, 204, 51));
					comp.cancel();
				}
			}
		}
		
	}
	
	public void flagger() {
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (pos[a][b] != ".") {
					aiscore[a][b] = -1;
				} else {
					aiscore[a][b] = 0;
				}
			}
		}
		
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (pos[x][y] != "." && pos[x][y] != " ") {
					
					int dotcount = 0;
					ArrayList<Integer> xvalue = new ArrayList<Integer>();
					ArrayList<Integer> yvalue = new ArrayList<Integer>();
					for (int a = 0; a < xvalue.size(); a++) {
						xvalue.remove(xvalue.get(a));
						yvalue.remove(yvalue.get(a));
					}
					
					if (y != 15) {
			            if (pos[x][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x);
			            	yvalue.add(y+1);
			            }
			        }
			        if (y != 0) {
			        	if (pos[x][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x);
			            	yvalue.add(y-1);
			            }
			        }
			        if (x != 15) {
			        	if (pos[x + 1][y] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y);
			            }
			        }
			        if (x != 0) {
			        	if (pos[x - 1][y] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y);
			            }
			        }
			        if (x != 15 && y != 15) {
			        	if (pos[x + 1][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y+1);
			            }
			        }
			        if (x != 0 && y != 0) {
			        	if (pos[x - 1][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y-1);
			            }
			        }
			        if (x != 0 && y != 15) {
			        	if (pos[x - 1][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y+1);
			            }
			        }
			        if (x != 15 && y != 0) {
			        	if (pos[x + 1][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y-1);
			            }
			        }
			        
			        if (dotcount == Integer.valueOf(pos[x][y]) && dotcount > 0) {
			        	for (int e = 0; e < dotcount; e++) {
			        		aiscore[xvalue.get(e)][yvalue.get(e)] = 2;
			        	}
			        }
					
				}
			}
		}
		
	}
	public void opensquares() {
		for (int x = 0; x < 16; x++) {
			for (int y = 0; y < 16; y++) {
				if (pos[x][y] != "." && pos[x][y] != " ") {
					
					int flaggercount = 0;
					int dotcount = 0;
					ArrayList<Integer> xvalue = new ArrayList<Integer>();
					ArrayList<Integer> yvalue = new ArrayList<Integer>();
					for (int a = 0; a < xvalue.size(); a++) {
						xvalue.remove(xvalue.get(a));
						yvalue.remove(yvalue.get(a));
					}
					
					if (y != 15) {
			            if (aiscore[x][y + 1] == 2) {
			            	flaggercount++;
			            }
			            if (pos[x][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x);
			            	yvalue.add(y+1);
			            }
			        }
			        if (y != 0) {
			        	if (aiscore[x][y - 1] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x);
			            	yvalue.add(y-1);
			            }
			        }
			        if (x != 15) {
			        	if (aiscore[x + 1][y] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x + 1][y] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y);
			            }
			        }
			        if (x != 0) {
			        	if (aiscore[x - 1][y] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x - 1][y] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y);
			            }
			        }
			        if (x != 15 && y != 15) {
			        	if (aiscore[x + 1][y + 1] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x + 1][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y+1);
			            }
			        }
			        if (x != 0 && y != 0) {
			        	if (aiscore[x - 1][y - 1] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x - 1][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y-1);
			            }
			        }
			        if (x != 0 && y != 15) {
			        	if (aiscore[x - 1][y + 1] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x - 1][y + 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x-1);
			            	yvalue.add(y+1);
			            }
			        }
			        if (x != 15 && y != 0) {
			        	if (aiscore[x + 1][y - 1] == 2) {
			        		flaggercount++;
			            }
			        	if (pos[x + 1][y - 1] == ".") {
			            	dotcount++;
			            	xvalue.add(x+1);
			            	yvalue.add(y-1);
			            }
			        }
			        
			        if (flaggercount == Integer.valueOf(pos[x][y]) && flaggercount > 0) {
			        	for (int e = 0; e < dotcount; e++) {
			        		if (aiscore[xvalue.get(e)][yvalue.get(e)] != 2) {
			        			aiscore[xvalue.get(e)][yvalue.get(e)] = 1;
			        		}
			        	}
			        } /*else if (Integer.valueOf(pos[x][y]) < dotcount){
			        	for (int e = 0; e < dotcount; e++) {
			        		if (aiscore[xvalue.get(e)][yvalue.get(e)] != 2) {
			        			aiscore[xvalue.get(e)][yvalue.get(e)] = -0.5;
			        		}
			        	}
			        }*/
					
				}
			}
		}
		
		System.out.println(" ");
		for (int i = 15; i > -1; i--) {
			System.out.println(aiscore[0][i] +" "+ aiscore[1][i] +" "+ aiscore[2][i] +" "+ aiscore[3][i] +" "+ aiscore[4][i] +" "+ aiscore[5][i] +" "+ aiscore[6][i] +" "+ aiscore[7][i] +" "+ aiscore[8][i] +" "+ aiscore[9][i] +" "+ aiscore[10][i] +" "+ aiscore[11][i] +" "+ aiscore[12][i] +" "+ aiscore[13][i] +" "+ aiscore[14][i] +" "+ aiscore[15][i]);
		}
	}
	
	public void move() {
		
		//SCORING SYSTEM
		// -1 -> not possible
		// 0 -> possible but not desirable (last straw move)
		// 1 -> safe to click
		// 2 -> flag
		
		double highestscore = -10;
		int index1 = 0;
		int index2 = 0;
		for (int a = 0; a < 16; a++) {
			for (int b = 0; b < 16; b++) {
				if (aiflagged[a][b] != "F") {
					
					if (aiscore[a][b] > highestscore) {
						highestscore = aiscore[a][b];
						index1 = a;
						index2 = b;
					}
						
				}
			}
		}
		
		System.out.println("move -> " + index1 + ", " + index2);
		if (highestscore == 2) {
			aiexecute(index1, index2, 1);
		} else {
			aiexecute(index1, index2, 0);
		}
	}
	
	
}
