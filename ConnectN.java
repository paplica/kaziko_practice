import java.util.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import java.util.Random.* ;

class Configure {
    // とりあえずデフォルトの値は20にしておく
    int edge = 50 ;
    int r_margin = 20 ;
    int l_margin = 20 ;
    int t_margin = 20 ;
    int b_margin = 20 ;

    Configure(String filename){
        File file = new File(filename) ;
        if(!file.exists()){
            //System.out.println("設定ファイルが見つかりません．") ;
            //System.out.println("すべてデフォルトの値を設定しました．") ;
            return ;
        }
        try{
            Scanner fileIn = new Scanner(file) ;
            while(fileIn.hasNextLine()){
                String line = fileIn.nextLine();
                //","で区切る＆","の前後に空白が１個以上あってもOK
                String [] str = line.split("[¥¥s]*,[¥¥s]*");
                
                if(str.length!=2){
                    System.out.println("解析不能な行:"+line);
                    continue;
                }
                
                int val = Integer.parseInt(str[1]);
                if(str[0].equalsIgnoreCase("edge")){
                    edge = val;
                }else if(str[0].equalsIgnoreCase("r_margin")){
                    r_margin = val;
                }else if(str[0].equalsIgnoreCase("l_margin")){
                    l_margin = val;
                }else if(str[0].equalsIgnoreCase("t_margin")){
                    t_margin = val;
                }else if(str[0].equalsIgnoreCase("b_margin")){
                    b_margin = val;
                }else{
                    System.out.println("設定不能:"+str[0]);
                }
            }
            

            fileIn.close() ;
        }catch(FileNotFoundException err){
            System.out.println(err) ;
        }
    }


    void print(){
        /*System.out.println("--碁盤の設定--") ;
        System.out.println("1マスの辺: "+edge) ;
        System.out.println("右マージン: "+r_margin) ;
        System.out.println("左マージン: "+l_margin) ;
        System.out.println("上マージン: "+t_margin) ;
        System.out.println("下マージン: "+b_margin) ;
        System.out.println("----") ;*/
    }
}

class Bom{
    Random r = new Random();
    int width;
    int height;
    int w1;
    int w2;
    int w3;
    int h1;
    int h2;
    int h3;
    Bom(int w,int h){
        width = w;
        height = h;
        //爆弾３つ設置
        w1 = r.nextInt(w);
        h1 = r.nextInt(h);
        do{
            w2 = r.nextInt(w);
            h2 = r.nextInt(h);
        }while(w1==w2 && h1==h2);
        do{
            w3 = r.nextInt(w);
            h3 = r.nextInt(h);
        }while((w3==w1 && h3==h1)||(w3==w2 && h3==h2));
        
        //爆弾答え
        System.out.println("("+w1+","+h1+")("+w2+","+h2+")("+w3+","+h3+")");
    }

    boolean check(int x,int y){
        if((x==w1&&y==h1)||(x==w2&&y==h2)||(x==w3&&y==h3)){
            return true;
        }
        return false;
    }

    int sign(int x,int y){
        int a=0;

        if((x-1==w1&&y-1==h1)||(x-1==w2&&y-1==h2)||(x-1==w3&&y-1==h3)){
            a++;
        }
        if((x==w1&&y-1==h1)||(x==w2&&y-1==h2)||(x==w3&&y-1==h3)){
            a++;
        }
        if((x+1==w1&&y-1==h1)||(x+1==w2&&y-1==h2)||(x+1==w3&&y-1==h3)){
            a++;
        }
        if((x-1==w1&&y==h1)||(x-1==w2&&y==h2)||(x-1==w3&&y==h3)){
            a++;
        }
        if((x+1==w1&&y==h1)||(x+1==w2&&y==h2)||(x+1==w3&&y==h3)){
            a++;
        }
        if((x-1==w1&&y+1==h1)||(x-1==w2&&y+1==h2)||(x-1==w3&&y+1==h3)){
            a++;
        }
        if((x==w1&&y+1==h1)||(x==w2&&y+1==h2)||(x==w3&&y+1==h3)){
            a++;
        }
        if((x+1==w1&&y+1==h1)||(x+1==w2&&y+1==h2)||(x+1==w3&&y+1==h3)){
            a++;
        }
        return a;
    }

}

class Board {
    int [][]state ;
    int width;
    int heigth;
    int win;
    int num;
    int current;
    final int no_stone =0;
    final int black =1;
    final int white = 2;
    final int error = -1;
    
    boolean bStart ;//勝負が始まったかどうか
    boolean bEnd ;//勝負が終わったかどうか
    int end = 0;//何回勝ち負けがついたか
    int winner ; //どっちが勝ったか
    Bom bom;
    
    //コンストラクタ
    Board(int w,int h,int n){
        state = new int[w][h];
        width = w;
        heigth = h;
        win = n;
        
        num = 0;
        current = black ;//先手は黒
        
        winner = no_stone;//勝者
        bStart = true;
        bEnd = false;
        bom = new Bom(w,h);
    }
    //勝者を決めるメソッド
    void setWinner(int color){
        bEnd = true;
        winner = color;
    }
    void setWinnerB(int color){
        bEnd = true;
        if(color==black)
            winner = white;
        else
            winner=black;
    }
    int getWinner(){
        return winner;
    }

    boolean isStart(){
        return bStart;
    }
    boolean isEnd(){
        return bEnd;
    }
    boolean isFull(){
        if(num<width*heigth)
          return false;
      return true;
  }

  boolean check(int x,int y,int stone){
    int i,j;//調べるマス
    //横
    int h= 1;//並んだ数
    int s= 1;
    int t= 1;
    int u= 1;
    
    //右方向
    i = x+1;
    j = y;
    //
    while(get(i,j)==stone){
        h++;
        i++;
    }
    //左方向
    i = x-1;
    j = y;
    //
    while(get(i,j)==stone){
        h++;
        i--;
    }

    //上方向
    i = x;
    j = y-1;
    //
    while(get(i,j)==stone){
        s++;
        j--;
    }
    //下方向
    i = x;
    j = y+1;
    //
    while(get(i,j)==stone){
        s++;
        j++;
    }

    //↗️方向
    i = x+1;
    j = y-1;
    //
    while(get(i,j)==stone){
        t++;
        i++;
        j--;
    }
    //↙️方向
    i = x-1;
    j = y+1;
    //
    while(get(i,j)==stone){
        t++;
        i--;
        j++;
    }

    //↖️方向
    i = x-1;
    j = y-1;
    //
    while(get(i,j)==stone){
        u++;
        i--;
        j--;
    }
    //↘️方向
    i = x+1;
    j = y+1;
    //
    while(get(i,j)==stone){
        u++;
        i++;
        j++;
    }

    
    //もしwin個以上並んでいたらtrue
    if(h >= win)
        return true;
    else if(s >= win)
        return true;
    else if(t >= win)
        return true;
    else if(u >= win)
        return true;

    return false;

}



void put(int x,int y,int stone){
    state[x][y] = stone;
    num++;
}
void turn(){
    if(current==black){
        current = white;
    }else{
        current = black;
    }
}
int get(int i,int j){
    if(i<0||j<0||i>=width||j>=heigth)
        return error;
    return state[i][j];
}
}

class SwingDraw extends JPanel {
    final int bstroke = 2 ; // 線の太さ
    MainFrame frame;
    final int s_margin = 2;//マスの余白
    //コンストラクタ
    SwingDraw(MainFrame f){
        frame = f;
    }
    
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        
        // 背景色の設定
        setBackground(Color.white) ;

        // ボードの描画
        Graphics2D g2 = (Graphics2D) g ;
        BasicStroke w = new BasicStroke(bstroke) ;
        g2.setStroke(w) ;
        g2.setColor(Color.darkGray);

        //縦の線
        for(int i=0;i<=frame.bd.width;i++){
            int sx = frame.cf.l_margin+i*frame.cf.edge;
            int sy = frame.cf.t_margin;
            int ex = sx;
            int ey = frame.cf.t_margin+frame.cf.edge*frame.bd.heigth;
            g2.drawLine(sx,sy,ex,ey);
        }
        
        //横の線
        for(int i=0;i<=frame.bd.heigth;i++){
            int sx = frame.cf.l_margin;
            int sy = frame.cf.t_margin+i*frame.cf.edge;
            int ex = frame.cf.l_margin+frame.cf.edge*frame.bd.width;
            int ey = sy;
            g2.drawLine(sx,sy,ex,ey);
        }
        
        for(int i=0;i<frame.bd.width;i++){
            for(int j=0;j<frame.bd.heigth;j++){
                if(frame.bd.get(i,j)!=frame.bd.no_stone){
                    int x = frame.cf.l_margin;
                    x += i*frame.cf.edge+s_margin;
                    int y = frame.cf.t_margin;
                    y += j*frame.cf.edge+s_margin;
                    int e = frame.cf.edge - 2*2;
                    
                    g2.setColor(Color.darkGray);
                    if(frame.bd.get(i, j)==frame.bd.black){
                        g2.fillOval(x, y, e, e);
                        g2.setColor(Color.white);
                        g2.fillOval(x+e/3, y+e/6, e/10, e/10);
                        g2.fillOval(x+e/2, y+e/6, e/10, e/10);
                    }else{
                        g2.drawOval(x,y,e,e);
                        g2.fillOval(x+e/3, y+e/6, e/10, e/10);
                        g2.fillOval(x+e/2, y+e/6, e/10, e/10);
                    }
                        int a =frame.bd.bom.sign(i,j);
                        if(a==1){
                            g2.setColor(Color.cyan);
                            g2.drawString("1", x+20, y+30);
                        }else if(a==2){
                            g2.setColor(Color.green);
                            g2.drawString("2", x+20, y+30);
                        }else if(a==3){
                            g2.setColor(Color.yellow);
                            g2.drawString("3", x+20, y+30);
                        }
                    
                    
                }
            }
        }
        //勝敗決定後
            if(frame.bd.isEnd()){
                int a = frame.bd.getWinner();
                g2.setColor(Color.red);
                if(a==1){
                    g2.drawString("black win",frame.cf.l_margin,frame.cf.t_margin);
                }else if(a==2){
                    g2.drawString("white win",frame.cf.l_margin,frame.cf.t_margin);
                }
                int h1= frame.cf.l_margin+frame.bd.bom.h1*frame.cf.edge;
                int w1= frame.cf.t_margin+frame.bd.bom.w1*frame.cf.edge;
                int h2= frame.cf.l_margin+frame.bd.bom.h2*frame.cf.edge;
                int w2= frame.cf.t_margin+frame.bd.bom.w2*frame.cf.edge;
                int h3= frame.cf.l_margin+frame.bd.bom.h3*frame.cf.edge;
                int w3= frame.cf.t_margin+frame.bd.bom.w3*frame.cf.edge;
                int e = frame.cf.edge;


                g2.drawLine(w1, h1 , w1+e, h1+e);
                g2.drawLine(w1, h1+e, w1+e, h1);
                g2.drawLine(w2, h2, w2+e, h2+e);
                g2.drawLine(w2, h2+e, w2+e, h2);
                g2.drawLine(w3, h3, w3+e, h3+e);
                g2.drawLine(w3, h3+e, w3+e, h3);
            }else if(frame.bd.isFull()){
                g2.setColor(Color.orange);
                g2.drawString("draw",frame.cf.l_margin,frame.cf.t_margin);
            }
        
    }
}


class MainFrame extends JFrame {
    Configure cf ; // マスなどのサイズ
    Board bd ; // 碁盤の情報
    SwingDraw panel;
    MainFrame(String title, Configure c, Board b){
        super(title) ;
        cf = c ;
        bd = b ;

        // パネルの作成＆追加
        panel = new SwingDraw(this) ;
        Container cp = getContentPane() ;
        cp.add(panel) ;
        
        //マウスリスナー
        MouseCheck  ms = new MouseCheck(this);
        panel.addMouseListener(ms);

        // ウィンドウを閉じた時の処理
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE) ;
        // ウィンドウの幅を計算
        int width = cf.edge*bd.width+cf.r_margin+cf.l_margin;

        // ウィンドウの高さを計算
        int height = cf.edge*bd.heigth+cf.t_margin+cf.b_margin;

        // 枠線とタイトルバーを除いてwidth×heightとなるようにする
        Dimension d = new Dimension(width, height) ;
        cp.setPreferredSize(d);
        pack() ;
        setVisible(true) ;
        // 確認用の出力
        System.out.println(width+"x"+height+"pixelのウィンドウを作りました．") ;
    }
}

class MouseCheck implements MouseListener{
    MainFrame frame;
    MouseCheck(MainFrame f){
        frame = f;
    }
    public void mouseEntered(MouseEvent e){
    }
    public void mouseExited(MouseEvent e){
    }
    public void mouseClicked(MouseEvent e){
    }
    public void mousePressed(MouseEvent e){
        int x =e.getX();
        int y = e.getY();
        int b = e.getButton();
        
        //もし別のボタンなら何もしない
        if(b!=MouseEvent.BUTTON1){
            return;
        }
        //もし決着済みならば何もしない
        if(frame.bd.isEnd()){
            return;
        }
        //マージンを引く
        x -= frame.cf.l_margin;
        y -= frame.cf.t_margin;
        //マス目に変換する
        int px = x/frame.cf.edge;
        int py = y/frame.cf.edge;
        //確認する
        System.out.print("横"+px+"マス目,縦"+py);
        System.out.println("です.");
        
        //枠外ならなにもしない
        if(px<0||px>=frame.bd.width||py<0||py>=frame.bd.heigth){
            return;
        }
        //座標が０未満の場合も何もしない
        if(x<0||y<0){
            return;
        }
        //既に置かれているなら何もしない
        if(frame.bd.get(px,py)!=frame.bd.no_stone){
            return;
        }
        
        //石をおく
        frame.bd.put(px,py,frame.bd.current);

        if(frame.bd.bom.check(px, py)){
            frame.bd.setWinnerB(frame.bd.current);
            if(frame.bd.current==frame.bd.black){
                System.out.println("白の勝ち");
            }else{
                System.out.println("黒の勝ち");
            }
        }
        if(frame.bd.check(px,py,frame.bd.current)){
            frame.bd.setWinner(frame.bd.current);
            
            if(frame.bd.current==frame.bd.black){
                System.out.println("黒の勝ち");
            }else{
                System.out.println("白の勝ち");
            }
        }else if(frame.bd.isFull()){
            frame.bd.setWinner(frame.bd.no_stone);
            System.out.print("引き分け");
        }else{
            frame.bd.turn();
        }
        
        //再描画       
        frame.panel.repaint();
    }
    public void mouseReleased(MouseEvent e){
    }    
}

public class ConnectN {
    public static void main(String [] args){
        Scanner stdIn = new Scanner(System.in) ;
        
        Configure cf = new Configure("config.txt");
        cf.print();
        int n,w,h;
        do{
            System.out.print("何目並べにしますか＝＞");
            n = stdIn.nextInt();
        }while(n<3);
        do{
            System.out.print("横は何マスにしますか＝＞");
            w = stdIn.nextInt();
        }while(w<n);
        do{
            System.out.print("縦は何マスにしますか＝＞");
            h = stdIn.nextInt();
        }while(h<n);

        Board bd = new Board(w,h,n);

    //ウィンドウの作成
        String title = String.valueOf(w);
        title += "x";
        title += String.valueOf(h);
        title += "のボードで";
        title += String.valueOf(n);
        title += "目並べ";
        MainFrame frame = new MainFrame(title,cf,bd);
    }
}
