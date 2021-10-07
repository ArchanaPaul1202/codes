import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.time.temporal.*;
import java.util.ArrayList;
import java.sql.*; 
import static java.time.DayOfWeek.SUNDAY;

class MyTableModel extends DefaultTableModel {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public MyTableModel() {
      super(new String[]{"Events", "Select"}, 0);
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
      Class clazz = String.class;
      switch (columnIndex) {
        case 0:
          clazz = String.class;
          break;
        case 1:
          clazz = Boolean.class;
          break;
      }
      return clazz;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
      return column == 1;
    }

  }

class Calendar implements ActionListener{
    static JFrame jf = new JFrame("Calendar");
    static JComboBox <String> month = new JComboBox<>();
    static JComboBox <Integer> year = new JComboBox<>();
    static JButton view = new JButton("VIEW");
    static JButton reset = new JButton("RESET");
    static JTextArea area = new JTextArea();
    public static int date=0;
    static Connection con;
    static JButton [] buttons = new JButton[35];
    static JFrame add_event;
    static JFrame modify_event;
    static JFrame jf2,delete_frame;
    static MyTableModel model;
    public static JTextPane  jt =  new JTextPane();;

    public static void main(String [] rk) throws SQLException {
        jf.setLayout(null);

        StyledDocument doc = jt.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);
        jt.setFont(new Font("Serif",1,17));
        con= DriverManager.getConnection("jdbc:mysql://localhost:3306/Calendar","root","root");
        for(Month m: java.time.Month.values())
        month.addItem(m.toString());
        for(int i=2000; i<2100; i++)
        year.addItem(i);

        JPanel bottom_main =  new JPanel();
        bottom_main.add(month);
        bottom_main.add(month);
        bottom_main.add(year); 
        bottom_main.add(view); 
        bottom_main.add(reset); 
        view.addActionListener(new Calendar());
        reset.addActionListener(new Calendar());
        jf.add(bottom_main);
        bottom_main.setBounds(0,480,1460,40);
        //Panel for Buttons

        JPanel p = new JPanel(new GridLayout(6, 7));
        jf.add(p); p.setBounds(20, 20, 1000, 400);

        //Buttons for Days

        for(DayOfWeek d: DayOfWeek.values())
        {
        JButton b;
        p.add(b = new JButton(d.toString())); 
        b.setFocusPainted(false);
        b.setForeground(Color.WHITE);
        b.setBackground(Color.BLACK);
        b.setFont(new Font("Serif", 1, 16));
        }
        // Buttons for Dates

        for(int i=0; i<35; i++)
        {
            buttons[i] = new JButton();
            buttons[i].setFocusPainted(false);
            buttons[i].setBackground(Color.WHITE);
            buttons[i].setFont(new Font("Serif", 1, 24));
            buttons[i].addActionListener(new Calendar());
            p.add(buttons[i]);
        }
        reset();

        JScrollPane jtp = new JScrollPane(jt);
        jt.setEditable(false);
        jf.add(jtp);
        jtp.setBounds(1070,40,300,430);
        jtp.setBackground(Color.BLACK);
        JLabel hint = new JLabel("This Week");
        hint.setFont(new Font("Serif", 1, 24));
        jf.add(hint);
        hint.setBounds(1170,0,300,50);
        jf.setSize(1460, 550);
        
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.getContentPane().setBackground(Color.WHITE);
        jf.setVisible(true);
        jf.setResizable(false);
        String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+LocalDate.now().getDayOfMonth() +" and month_="+LocalDate.now().getMonthValue()+" and year_="+LocalDate.now().getYear();
        try{
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement);
            while(rs.next()){
                JOptionPane.showMessageDialog(jf,rs.getString("event_"));
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

public static void reset(){
    LocalDate ld = LocalDate.now();
    YearMonth default_yearMonth = YearMonth.of(ld.getYear(),ld.getMonthValue());
    int default_daysInMonth = default_yearMonth.lengthOfMonth();
    int current_date = ld.getDayOfMonth();
    month.setSelectedIndex(ld.getMonthValue()-1);
    year.setSelectedItem(ld.getYear());

    LocalDate localDate = LocalDate.of(ld.getYear(), ld.getMonthValue(),1);
    int default_dayOfWeek = localDate.getDayOfWeek().getValue()-1;
    int date = 1;
    for(int i=0;i<35;i++){
        buttons[i].setText("");
        buttons[i].setBackground(Color.WHITE);
    }
    for(int i=default_dayOfWeek;i<(default_daysInMonth+default_dayOfWeek);i++){
        if(i>34){
            buttons[i%35].setText(String.valueOf(date));
            buttons[i%35].setBackground(Color.LIGHT_GRAY);
        }
        else{
            buttons[i].setText(String.valueOf(date));
            buttons[i].setBackground(Color.LIGHT_GRAY);
        }
        if((i+1)%7==0){
            buttons[i].setForeground(Color.RED);
            buttons[i].setBackground(Color.LIGHT_GRAY);
        }
        date+=1;
    }
    buttons[LocalDate.now().getDayOfMonth()+2].setBackground(Color.GRAY);
    String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE month_="+LocalDate.now().getMonthValue()+" and year_="+LocalDate.now().getYear();
    try{
        Statement smt = con.createStatement();
        ResultSet rs = smt.executeQuery(sql_statement);
        while(rs.next()){
                int got_date=rs.getInt("date_");
                buttons[(got_date+default_dayOfWeek)-1].setBackground(Color.ORANGE);
        }
    }
    catch(Exception e){
        System.out.println(e.getMessage());
    }
    int month_now = LocalDate.now().getMonthValue(); 
    LocalDate date_sunday = LocalDate.now().with(TemporalAdjusters.nextOrSame(SUNDAY));
    int exact_sunday_date=date_sunday.getDayOfMonth();
    if(exact_sunday_date<LocalDate.now().getDayOfMonth()){
        exact_sunday_date+=LocalDate.now().getDayOfMonth()+(LocalDate.now().lengthOfMonth()-LocalDate.now().getDayOfMonth());
    }
    String to_put_jt="";
    for(int i=current_date;i<=exact_sunday_date;i++){
        if(i>LocalDate.now().lengthOfMonth()){
            i=1;
            month_now+=1;
            exact_sunday_date=date_sunday.getDayOfMonth();
        }
        String sql_statement_="SELECT event_ FROM CALENDAR_EVENTS WHERE date_="+i +" and month_="+month_now+" and year_="+LocalDate.now().getYear();
        try{
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement_);
           if(rs.next()==true){
            to_put_jt+=i+"-"+LocalDate.now().getMonthValue()+"-"+LocalDate.now().getYear()+"\n";
            System.out.println(rs.getString("event_"));
            to_put_jt+=rs.getString("event_");
            to_put_jt+="\n\n";
           }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
    if(to_put_jt.equals("")){
        Calendar.jt.setText("No Events This Week");
    }
    else{
        Calendar.jt.setText(to_put_jt);
    }

}
public static void makeEvent(int date, int month, int year){
    jf2 = new JFrame("Event");
    jf2.setLayout(null);
    jf2.setSize(400,300);
    jf2.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    jf2.setLocationRelativeTo(jf);
    JLabel Date = new JLabel("DATE: "+ String.valueOf(date)+"/"+String.valueOf(month)+"/"+String.valueOf(year));
    Date.setFont(new Font("Serif",1,17));
    jf2.add(Date);
    Date.setBounds(130,0,400,100);


    area=new JTextArea();

    String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+date+" and month_="+month+" and year_="+year;
    try{
        Statement smt = con.createStatement();
        ResultSet rs = smt.executeQuery(sql_statement);
        String to_put="";
        while(rs.next()){
                to_put+=rs.getString("event_");
                if(rs.next()){
                to_put+="\n";
                }
        }
       area.setText(to_put);
    }
    catch(Exception e){
        System.out.println(e.getMessage());
    }

    JPanel bottom_bar = new JPanel();
    JButton add_event= new JButton("Add Event");
    add_event.setFocusPainted(false);
    add_event.addActionListener(new Calendar());
    bottom_bar.add(add_event);
    area.setEditable(false);
    if(area.getText().length()!=0){
        area.setFont(new Font("Serif",1,20));
        JScrollPane scrollPane = new JScrollPane(area);
        jf2.add(scrollPane);
        scrollPane.setBounds(20,100,345,120);
        JButton modify_event= new JButton("Modify Event");
        modify_event.setFocusPainted(false);
        modify_event.addActionListener(new Calendar());
        bottom_bar.add(modify_event);
        JButton delete_event= new JButton("Delete Event");
        delete_event.setFocusPainted(false);
        delete_event.addActionListener(new Calendar());
        bottom_bar.add(delete_event);
    }
    else{
        JLabel no_event = new JLabel("There is no event to display");
        no_event.setFont(new Font("Serif",1,15));
        jf2.add(no_event);
        no_event.setBounds(110,50,400,100);
    }
    jf2.add(bottom_bar);
    bottom_bar.setBounds(0,220,400,80);
    jf2.getContentPane().setBackground(new Color(255,255,255));
    jf2.setResizable(false);
    jf2.setVisible(true);

}

public void refresh(){
    int selected_month=month.getSelectedIndex()+1;
    int year_ = (int) (year.getSelectedItem());
    if(selected_month==LocalDate.now().getMonthValue() && year_==LocalDate.now().getYear()){
        reset();
    }
    else{
        YearMonth yearMonth = YearMonth.of(year_,selected_month);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate localDate = LocalDate.of(year_, selected_month, 01);
        int dayOfWeek = localDate.getDayOfWeek().getValue()-1;
        date = 1;
        for(int i=0;i<35;i++){
            buttons[i].setText("");
            buttons[i].setBackground(Color.WHITE);
        }
        for(int i=dayOfWeek;i<(daysInMonth+dayOfWeek);i++){
            if(i>34){
                buttons[i%35].setBackground(Color.LIGHT_GRAY);
                buttons[i%35].setText(String.valueOf(date));
            }
            else{
                buttons[i].setText(String.valueOf(date));
                buttons[i].setBackground(Color.LIGHT_GRAY);
            }
            if((i+1)%7==0){
                buttons[i].setForeground(Color.RED);
                buttons[i].setBackground(Color.LIGHT_GRAY);
            }
            
            date+=1;
        }
        String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE month_="+selected_month+" and year_="+year_;
        try{
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement);
            while(rs.next()){
                    int got_date=rs.getInt("date_");
                    buttons[(got_date+dayOfWeek)-1].setBackground(Color.ORANGE);
            }
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
    }
}

public void delete_all(JFrame passed_frame, String what_action){
    int selected_month=month.getSelectedIndex()+1;
    int year_ = (int) (year.getSelectedItem());
    try{
        PreparedStatement insert_db = con.prepareStatement("DELETE FROM CALENDAR_EVENTS WHERE date_=? and month_=? and year_=?");
        insert_db.setInt(1, Calendar.date);
        insert_db.setInt(2, selected_month);
        insert_db.setInt(3, year_);
        int done=insert_db.executeUpdate();
        if(done>0){
            JOptionPane.showMessageDialog(jf,"Event "+what_action);
        }
        else{
            JOptionPane.showMessageDialog(jf,"Can't "+what_action+" Event");
        }
        refresh();
        jf2.dispose();
        passed_frame.dispose();
        refresh();
    }
    catch(Exception ex){
        System.out.println(ex.getMessage());
    }
}
public void actionPerformed(ActionEvent e)
{   area.setEditable(true);
    int selected_month=month.getSelectedIndex()+1;
    int year_ = (int) (year.getSelectedItem());
    for(int i=0;i<35;i++){
        if(e.getActionCommand().equals(String.valueOf(i))){	
            Calendar.date=i;
            makeEvent(date,selected_month,year_);
        }
    }

    if(e.getActionCommand().equals("Add Event")){
        add_event = new JFrame("Add Event");
        add_event.setLayout(null);
        add_event.setSize(400,300);
        add_event.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add_event.setLocationRelativeTo(jf);

        JLabel label = new JLabel("ADD EVENT");
        label.setFont(new Font("Serif",1,17));
        add_event.add(label);
        label.setBounds(130,0,400,100);

        JLabel Date = new JLabel("DATE: "+ String.valueOf(Calendar.date)+"/"+String.valueOf(selected_month)+"/"+String.valueOf(year_));
        Date.setFont(new Font("Serif",1,17));
        add_event.add(Date);
        Date.setBounds(120,25,400,100);
        area.setText("");
        area.setFont(new Font("Serif",1,20));
        JScrollPane scrollPane = new JScrollPane(area);
        add_event.add(scrollPane);
        scrollPane.setBounds(20,100,345,120);
        JPanel bottom_bar = new JPanel();
        JButton save= new JButton("Save Event");
        save.addActionListener(new Calendar());
        save.setFocusPainted(false);
        bottom_bar.add(save);
        add_event.add(bottom_bar);
        bottom_bar.setBounds(0,220,400,80);
        add_event.getContentPane().setBackground(new Color(255,255,255));
        add_event.setResizable(false);
        add_event.setVisible(true);
    }
    if(e.getActionCommand().equals("Save Event")){
        Boolean input_dupli=false;
        String [] from_area = Calendar.area.getText().split("\n",0);
        for(int i=0;i<from_area.length;i++){
            if(input_dupli){
                break;
            }
            for(int j=0;j<from_area.length;j++){
                if(i!=j){
                    if(from_area[i].equals(from_area[j])){
                        input_dupli=true;
                        JOptionPane.showMessageDialog(jf,"Duplicate Event Exists: "+from_area[i]);
                        jf2.dispose();
                        break;
                    }
                }
            }
        }
        if(!input_dupli){
        try{
            Boolean flag=true;
            String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+Calendar.date+" and month_="+selected_month+" and year_="+year_;
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement);
            String [] entered_inputs = Calendar.area.getText().split("\n",0);
            while(rs.next()){
                String [] val = rs.getString("event_").split("\n",0);
                for(String s :  entered_inputs){
                    if(flag==false){
                        break;
                    }
                    for(String k : val){
                    if(k.equals(s)){
                        flag=false;
                        JOptionPane.showMessageDialog(jf,"Event Already Exists: "+k);
                        jf2.dispose();
                        break;
                    }
                }
            }
            }
            if(flag){
                int count=0;
                String prev_val="";
                sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+Calendar.date+" and month_="+selected_month+" and year_="+year_;
                try{
                     smt = con.createStatement();
                     rs = smt.executeQuery(sql_statement);
                    while(rs.next()){
                        count+=1;
                        prev_val+=rs.getString("event_");
                        if (rs.next()){
                            prev_val+="\n";
                        }
                    }
                }
                catch(Exception ex){
                    System.out.println(ex.getMessage());
                } 
                if(count==0){                
                    String [] get_all_events=Calendar.area.getText().split("^\\n+",2);
                    String removed_starting_line="";
                    for(int i=0;i<get_all_events.length;i++){
                      removed_starting_line+=get_all_events[i];
                    }
                    get_all_events = removed_starting_line.split("\\n+$",2);
                    String removed_extra_line="";
                    for(int i=0;i<get_all_events.length;i++){
                        removed_extra_line+=get_all_events[i];
                      }
                    removed_extra_line=removed_extra_line.replaceAll("(\r?\n)+", "\n");
                    
                    PreparedStatement insert_db = con.prepareStatement("INSERT INTO CALENDAR_EVENTS VALUES (?,?,?,?)");
                    insert_db.setInt(1, Calendar.date);
                    insert_db.setInt(2, selected_month);
                    insert_db.setInt(3, year_);
                    insert_db.setString(4, removed_extra_line);
                    int inserted_done=insert_db.executeUpdate();
                    if(inserted_done>0){
                        JOptionPane.showMessageDialog(jf,"Event Added");
                    }
                    else{
                        JOptionPane.showMessageDialog(jf,"Can't Add Event");
                    }
                    add_event.dispose();
                    jf2.dispose();
                    refresh();
                   }
                else{
                    String new_val=prev_val+"\n"+Calendar.area.getText();
                    try{
                        PreparedStatement updateEvent = con.prepareStatement("update CALENDAR_EVENTS set event_ =?  where date_ = ? and month_=? and year_=?");
                        updateEvent.setString(1,new_val);
                        updateEvent.setInt(2, Calendar.date);  
                        updateEvent.setInt(3, selected_month );
                        updateEvent.setInt(4, year_); 
                        int done=updateEvent.executeUpdate();
                        if(done>0){
                            JOptionPane.showMessageDialog(jf,"Event Added");
                        }
                        else{
                            JOptionPane.showMessageDialog(jf,"Can't Add Event");
                        }
                        jf2.dispose(); 
                        add_event.dispose();
                        refresh();
                    }
                    catch(Exception ex){
                        System.out.println(ex.getMessage());
                    }
                }
             }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    }

    if(e.getActionCommand().equals("Modify Event")){
        modify_event = new JFrame("Modify Event");
        modify_event.setLayout(null);
        modify_event.setSize(400,300);
        modify_event.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        modify_event.setLocationRelativeTo(jf);

        JLabel label = new JLabel("MODIFY EVENT");
        label.setFont(new Font("Serif",1,17));
        modify_event.add(label);
        label.setBounds(120,0,400,100);

        JLabel Date = new JLabel("DATE: "+ String.valueOf(Calendar.date)+"/"+String.valueOf(selected_month)+"/"+String.valueOf(year_));
        Date.setFont(new Font("Serif",1,17));
        modify_event.add(Date);
        Date.setBounds(120,25,400,100);
        area.setFont(new Font("Serif",1,20));
        Calendar.area.setText("");

        String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+Calendar.date+" and month_="+selected_month+" and year_="+year_;
        try{
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement);
            String to_put="";
            while(rs.next()){
                    to_put+=rs.getString("event_");
                    if(rs.next()){
                    to_put+="\n";
                    }
            }
           Calendar.area.setText(to_put);
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

        JScrollPane scrollPane = new JScrollPane(area);
        modify_event.add(scrollPane);
        scrollPane.setBounds(20,100,345,120);
        JPanel bottom_bar = new JPanel();
        JButton save= new JButton("Apply Changes");
        save.setFocusPainted(false);
        save.addActionListener(new Calendar());
        bottom_bar.add(save);
        modify_event.add(bottom_bar);
        bottom_bar.setBounds(0,220,400,80);
        modify_event.getContentPane().setBackground(new Color(255,255,255));
        modify_event.setResizable(false);
        modify_event.setVisible(true);
    }

    if(e.getActionCommand().equals("Apply Changes")){
        if(!Calendar.area.getText().isBlank()){
        try{
            PreparedStatement updateEvent = con.prepareStatement("update CALENDAR_EVENTS set event_ =?  where date_ = ? and month_=? and year_=?");
            updateEvent.setString(1, Calendar.area.getText());
            updateEvent.setInt(2, Calendar.date);  
            updateEvent.setInt(3, selected_month );
            updateEvent.setInt(4, year_); 
            int done=updateEvent.executeUpdate();
            if(done>0){
                JOptionPane.showMessageDialog(jf,"Event Modified");
            }
            else{
                JOptionPane.showMessageDialog(jf,"Can't Modify Event");
            }
            jf2.dispose(); 
            modify_event.dispose();
            refresh();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
    }
    else{
        delete_all(modify_event,"Modify");
    }
    }

    if(e.getActionCommand().equals("Delete Event")){
        String [] array_events = area.getText().toString().split("\n",0);
        model = new MyTableModel();
        for(int i=0;i<array_events.length;i++){
            model.addRow(new Object[]{array_events[i], false});
        }
        JTable table = new JTable(model); 
        delete_frame = new JFrame("Delete Event");
        delete_frame.setLayout(null);
        delete_frame.setSize(400,300);
        delete_frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        delete_frame.setLocationRelativeTo(jf);

        JLabel label = new JLabel("Delete EVENT");
        label.setFont(new Font("Serif",1,17));
        delete_frame.add(label);
        label.setBounds(120,0,400,100);

        JLabel Date = new JLabel("DATE: "+ String.valueOf(Calendar.date)+"/"+String.valueOf(selected_month)+"/"+String.valueOf(year_));
        Date.setFont(new Font("Serif",1,17));
        delete_frame.add(Date);
        Date.setBounds(120,25,400,100);
        JScrollPane scrollPane = new JScrollPane(table);
        delete_frame.add(scrollPane);
        scrollPane.setBounds(20,100,345,120);
        JPanel bottom_bar = new JPanel();
        JButton save= new JButton("Delete Selected");
        save.setFocusPainted(false);
        JButton delete_all= new JButton("Delete All");
        delete_all.setFocusPainted(false);
        save.addActionListener(new Calendar());
        delete_all.addActionListener(new Calendar());
        bottom_bar.add(delete_all);
        bottom_bar.add(save);
        delete_frame.add(bottom_bar);
        bottom_bar.setBounds(0,220,400,80);
        delete_frame.getContentPane().setBackground(new Color(255,255,255));
        delete_frame.setResizable(false);
        delete_frame.setVisible(true);
    }
    if(e.getActionCommand().equals("Delete Selected")){
        String sql_statement="SELECT * FROM CALENDAR_EVENTS WHERE date_="+Calendar.date+" and month_="+selected_month+" and year_="+year_;
        String res=""; 
        int selected_count=0;
        int total_res=0;
        try{
            Statement smt = con.createStatement();
            ResultSet rs = smt.executeQuery(sql_statement);
            while(rs.next()){
                    res=rs.getString("event_").toString();
            }
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }
        String [] array_events = res.split("\n",0); 
        total_res=array_events.length;
        ArrayList<String> new_array = new ArrayList<String>();
        for(int i=0;i<array_events.length;i++){
            Boolean exclude = (Boolean) Calendar.model.getValueAt(i, 1);
            if(!exclude){
                new_array.add(array_events[i]);
            }
            else{
                selected_count++;
            }
        }
        if(selected_count==total_res){
            delete_all(delete_frame, "Deleted");
        }
        else{
            if(selected_count==0){
                JOptionPane.showMessageDialog(jf,"Nothing Selected");
            }
            else{
            String update="";
            for(int i=0;i<new_array.size();i++){
                update+=new_array.get(i);
                if(i!=new_array.size()-1) update+="\n";
            }

            try{
                PreparedStatement updateEvent = con.prepareStatement("update CALENDAR_EVENTS set event_ =?  where date_ = ? and month_=? and year_=?");
                updateEvent.setString(1,update);
                updateEvent.setInt(2, Calendar.date);  
                updateEvent.setInt(3, selected_month );
                updateEvent.setInt(4, year_); 
                int done=updateEvent.executeUpdate();
                if(done>0){
                    JOptionPane.showMessageDialog(jf,"Selected Event Deleted");
                }
                else{
                    JOptionPane.showMessageDialog(jf,"Can't Delete Event");
                }
                jf2.dispose(); 
                delete_frame.dispose();
                refresh();
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        }
        }
    }
    if(e.getActionCommand().equals("Delete All")){
       delete_all(delete_frame,"Deleted");
    }
    if(e.getActionCommand().equals("VIEW")){
        refresh();
    }
    if(e.getActionCommand().equals("RESET")){
        reset();
    }
}
}