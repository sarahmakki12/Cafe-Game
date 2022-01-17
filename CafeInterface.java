/*Cafe RPG Game
Mr. Jay
ICS4U1-02
Sarah Ali
June 2020*/

package com.company;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class CafeInterface extends JFrame implements KeyListener {
    int score = 0, lives = 3; //accumulators for score and lives left

    DrawArea board = new DrawArea(1000, 500); //Area for cafe to be displayed
    Inventory inventory = new Inventory(200, 109); //Area for inventory to be displayed
    Cafe cafe = new Cafe(5, 8, 125, 100); //create cafe, block size

    //create/initialize components
    JTextField scoreText = new JTextField(2);
    JTextField livesText = new JTextField(1);
    JTextArea msg = new JTextArea(4, 39);

    JLabel star;
    JLabel heart;

    JPanel scorePanel = new JPanel();
    JPanel livesPanel = new JPanel();
    JPanel north = new JPanel();
    JPanel south = new JPanel();

    public CafeInterface(String title) {
        super(title);

        //set layouts
        setLayout(new BorderLayout(0, 0));
        scorePanel.setLayout(new FlowLayout());
        livesPanel.setLayout(new FlowLayout());
        north.setLayout(new BorderLayout());
        south.setLayout(new FlowLayout());

        //initialize/add images
        try {
            star = new JLabel(new ImageIcon(ImageIO.read(new File("src/com/company/Elements/star.png"))));
            scorePanel.add(star);
            heart = new JLabel(new ImageIcon(ImageIO.read(new File("src/com/company/Elements/heart.png"))));
            livesPanel.add(heart);
        } catch (IOException e) {
            System.out.println("File not found");
        }

        //add components
        scorePanel.add(scoreText);

        livesPanel.add(livesText);

        north.add(scorePanel, BorderLayout.WEST);
        north.add(livesPanel, BorderLayout.EAST);

        south.add(msg);
        south.add(inventory);

        add(north, BorderLayout.NORTH);
        add(board, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //set text attributes
        scoreText.setFont(new Font("SansSerif", Font.BOLD, 30));
        livesText.setFont(new Font("SansSerif", Font.BOLD, 30));
        msg.setFont(new Font("SansSerif", Font.BOLD, 20));

        msg.setBorder(BorderFactory.createLineBorder(Color.black, 3));

        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);

        scoreText.setEditable(false);
        livesText.setEditable(false);
        msg.setEditable(false);

        scoreText.setText(String.valueOf(score));
        livesText.setText(String.valueOf(lives));

        //enable key listener for movement
        addKeyListener(this);
        setFocusable(true);
        setFocusTraversalKeysEnabled(false);
        board.addKeyListener(this);
    }

    public void keyTyped(KeyEvent e) {

    }

    public void keyPressed(KeyEvent e) { //handles cursor keys
        if (lives > 0 && score < 20) { //game is only playable when lives are left and when score is not at the maximum of 20
            int key = e.getKeyCode();
            switch (key) {
                case KeyEvent.VK_UP:
                    msg.setText(cafe.move(0, -1));
                    break;
                case KeyEvent.VK_DOWN:
                    msg.setText(cafe.move(0, 1));
                    break;
                case KeyEvent.VK_LEFT:
                    msg.setText(cafe.move(-1, 0));
                    break;
                case KeyEvent.VK_RIGHT:
                    msg.setText(cafe.move(1, 0));
                    break;
            }

            if (msg.getText().equals("Customer served! +1 Score")) { //score and lives adjusted
                score++;
                scoreText.setText(String.valueOf(score));
            } else if (msg.getText().equals("Wrong order! Customer lost! -1 Life")) {
                lives--;
                livesText.setText(String.valueOf(lives));
            }

            if (score == 20) { //game ends when player reaches maximum score of 20
                msg.setForeground(Color.green);
                msg.setText("Congratulations! You have completed the game!");
            }

            if (lives == 0) { //game over when all lives are lost
                msg.setForeground(Color.red);
                msg.setText("Game Over: You have lost all lives! Exit the game to start again");
            }

            repaint();
        }
    }

    public void keyReleased(KeyEvent e) {

    }

    class DrawArea extends JPanel { //drawing area for game
        public DrawArea(int width, int height) {
            this.setPreferredSize(new Dimension(width, height)); // size
            setBorder(BorderFactory.createLineBorder(Color.black, 3));
        }

        public void paintComponent(Graphics g) {
            cafe.print(g);
        }
    }

    class Inventory extends JPanel { //drawing area for inventory
        public Inventory(int width, int height) {
            this.setPreferredSize(new Dimension(width, height));
            setBorder(BorderFactory.createLineBorder(Color.black, 3));
        }

        public void paintComponent(Graphics g) {
            cafe.showInventory(g);
        }

    }

    //----------------------------------main--------------------------------
    public static void main(String[] args) {
        CafeInterface window = new CafeInterface("Cafe");
        window.setVisible(true);
        window.setSize(1000, 720);
        window.setLocationRelativeTo(null);
        window.setResizable(false);
    }
}

//----------------------------------Cafe class-------------------------------
class Cafe {
    private final Square[][] cafe;
    private final int width, height;
    private int posx, posy;
    private final Image[] image = new Image[9];
    private final Waiter waiter;

    public Cafe(int rows, int cols, int blockwidth, int blockheight) { //constructor
        width = blockwidth;
        height = blockheight;
        cafe = new Square[rows][cols]; //define 2d array

        loadImages();

        for (int i = 0; i < rows; i++) { //add flooring
            for (int j = 0; j < cols; j++) {
                cafe[i][j] = new Square();
            }
        }

        //add waiter and define position
        waiter = new Waiter();
        posy = 2;
        posx = 2;
        cafe[posy][posx] = waiter;

        //add tables along the top
        cafe[0][2] = new Table();
        cafe[0][4] = new Table();
        cafe[0][6] = new Table();

        //add food along the bottom
        cafe[4][2] = new Food("Coffee");
        cafe[4][4] = new Food("Tea");
        cafe[4][6] = new Food("Smoothie");

        //add initial customers along the left side
        cafe[1][0] = new Customer();
        cafe[2][0] = new Customer();
        cafe[3][0] = new Customer();
    }

    private void loadImages() { //load all images in array
        try {
            for (int i = 0; i < 3; i++) {
                image[i] = ImageIO.read(new File("src/com/company/Elements/customer" + (i + 1) + ".png"));
            }
            image[3] = ImageIO.read(new File("src/com/company/Elements/waiter.png"));
            image[4] = ImageIO.read(new File("src/com/company/Elements/table.png"));
            image[5] = ImageIO.read(new File("src/com/company/Elements/coffee.png"));
            image[6] = ImageIO.read(new File("src/com/company/Elements/tea.png"));
            image[7] = ImageIO.read(new File("src/com/company/Elements/smoothie.png"));
            image[8] = ImageIO.read(new File("src/com/company/Elements/floor.png"));
        } catch (IOException e) {
            System.out.println("File not found");
        }
    }

    public void print(Graphics g) { //displays cafe on screen
        for (int i = 0; i < cafe.length; i++) { //number of rows
            for (int j = 0; j < cafe[i].length; j++) { //length of row
                g.drawImage(cafe[i][j].getImage(), j * width, i * height, null); //draw object
            }
        }
    }

    public String move(int dx, int dy) { //moves waiter and initiates actions
        try {
            Square d = cafe[posy + dy][posx + dx]; //what's in front of the waiter
            if (d instanceof Food) {
                if (waiter.isCarrying) {
                    if (waiter.carry instanceof Food && waiter.carry.equals(d)) {
                        waiter.give(); //dropping food
                    } else {
                        return "You can only pick up one thing at a time";
                    }
                } else {
                    waiter.pickup(d); //picking up food
                }
            } else if (d instanceof Customer) {
                if (waiter.isCarrying) {
                    if (waiter.carry instanceof Food) {
                        return "You must serve the customer at the table";
                    }
                    return "You can only pick up one thing at a time";
                } else {
                    waiter.pickup(d); //picking up customer
                    cafe[posy + dy][posx + dx] = new Square();
                }
            } else if (d instanceof Table) {
                if (waiter.isCarrying) {
                    if (waiter.carry instanceof Customer) {
                        if (((Table) d).isOccupied) {
                            return "This table is already occupied";
                        } else {
                            ((Table) cafe[posy + dy][posx + dx]).seat((Customer) waiter.give()); //seating customer
                        }
                    } else {
                        if (((Table) cafe[posy + dy][posx + dx]).serve((Food) waiter.give())) { //serving customer
                            return "Customer served! +1 Score";
                        }
                        return "Wrong order! Customer lost! -1 Life";
                    }
                }
            } else { //regular movement
                posy += dy;
                posx += dx;
                cafe[posy][posx] = waiter;
                cafe[posy - dy][posx - dx] = new Square();
            }
        } catch (IndexOutOfBoundsException ignored) { //try catch since there's no wall
        }
        return " ";
    }

    public void showInventory(Graphics g) { //displays inventory on screen
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("Inventory", 5, 20);

        if (waiter.isCarrying) {
            g.drawImage(waiter.carry.getImage(), 30, 5, null);
        }
    }

    public void refillCustomers() { //more customers line up when a customer is served
        for (int i = 1; i <= 3; i++) {
            if (!cafe[i][0].item) {
                cafe[i][0] = new Customer();
                break;
            }
        }
    }

    class Square { //general parent Square class
        protected boolean item;
        protected Image tile;

        public Square() {
            item = false;
            tile = image[8];
        }

        public Image getImage() { //returns image of object
            return tile;
        }
    }

    class Waiter extends Square { //Waiter class is-a Square
        private boolean isCarrying;
        private Square carry;

        public Waiter() {
            item = true;
            tile = image[3];
            isCarrying = false;
            carry = null;
        }

        public void pickup(Square pick) { //picks up object
            carry = pick;
            isCarrying = true;
        }

        public Square give() { //returns object from inventory
            isCarrying = false;
            return carry;
        }
    }

    class Food extends Square { //Food class is-a Square
        private final String type;

        public Food(String order) {
            item = true;
            type = order;
            tile = image[type.equals("Coffee") ? 5 : (type.equals("Tea") ? 6 : 7)];
        }

        public boolean equals(Food compare) { //checks if food matches
            return compare.type.equals(type);
        }
    }

    class Table extends Square { //Table class is-a Square
        private boolean isOccupied;
        private Customer occupant;

        public Table() {
            item = true;
            tile = image[4];
            isOccupied = false;
            occupant = null;
        }

        public void seat(Customer sitter) { //seats customer and displays customer in table's place
            occupant = sitter;
            isOccupied = true;
            tile = sitter.tile;
        }

        public boolean serve(Food food) { //serves customer, invites another customer, and checks if order is correct
            boolean served = food.equals(occupant.order);
            occupant = null;
            isOccupied = false;
            tile = image[4];
            refillCustomers();
            return served;
        }
    }

    class Customer extends Square { //Customer class is-a Square
        private final Food order;

        public Customer() {
            item = true;
            int randomCustom = (int) (Math.random() * 3);
            tile = image[randomCustom];
            order = new Food(randomCustom == 0 ? "Coffee" : (randomCustom == 1 ? "Tea" : "Smoothie"));
        }
    }
}
