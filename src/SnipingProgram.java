import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import org.json.JSONException;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class SnipingProgram implements ActionListener {

	static JTextField tf;
	static JPasswordField tf2;
	static JTextField tf4;
	static JLabel l;
	static JLabel pl;
	static JTextField tf5;
	
	static String systemipaddress = ""; 

	static long pinglatency;

	static long[] avgping = new long[10];

	public static void main(String[] args) {
		
        try
        { 
            URL url_name = new URL("http://bot.whatismyipaddress.com"); 
  
            BufferedReader sc = 
            new BufferedReader(new InputStreamReader(url_name.openStream())); 
  
            // reads system IPAddress 
            systemipaddress = sc.readLine().trim(); 
            
            System.out.println(systemipaddress);            
            
            long sum = 0;

    		for (int i = 0; i < 10; i++) {
    			avgping[i] = getPing("api.mojang.com");
    		}

    		for (int i = 0; i < avgping.length; i++) {
    			sum += avgping[i];
    		}

    		pinglatency = sum / avgping.length;

    		System.out.println(pinglatency);

    		setupGUI();
        } 
        catch (Exception e) 
        { 
        	System.out.println(e);
        	JOptionPane.showMessageDialog(null, "Couldn't get public ip address!", "Please connect to wifi.",
					JOptionPane.ERROR_MESSAGE);
        } 
	}
	
	public static void startSnipe(String u, String p, String u2, String at, String d) {
		// sets up date and times
		
		try {
			
			String response = Unirest.get("http://worldtimeapi.org/api/ip/"+systemipaddress+".json").asJson().getBody().getObject().getString("datetime");
			
			DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss:SSSS");
			String s = d + ":0000";
			LocalDateTime dt = LocalDateTime.parse(s, format);
			LocalDateTime dtt = dt.minus(pinglatency, ChronoField.MILLI_OF_DAY.getBaseUnit());
			
			LocalDateTime dt2 = LocalDateTime.now();
			
			LocalDateTime fromDateTime = LocalDateTime.now();
			LocalDateTime toDateTime = LocalDateTime.parse(response.substring(0, 24));

			LocalDateTime tempDateTime = LocalDateTime.from( fromDateTime );

			long years = tempDateTime.until( toDateTime, ChronoUnit.YEARS );
			tempDateTime = tempDateTime.plusYears( years );

			long months = tempDateTime.until( toDateTime, ChronoUnit.MONTHS );
			tempDateTime = tempDateTime.plusMonths( months );

			long days = tempDateTime.until( toDateTime, ChronoUnit.DAYS );
			tempDateTime = tempDateTime.plusDays( days );


			long hours = tempDateTime.until( toDateTime, ChronoUnit.HOURS );
			tempDateTime = tempDateTime.plusHours( hours );

			long minutes = tempDateTime.until( toDateTime, ChronoUnit.MINUTES );
			tempDateTime = tempDateTime.plusMinutes( minutes );

			long seconds = tempDateTime.until( toDateTime, ChronoUnit.SECONDS );
			tempDateTime = tempDateTime.plusSeconds(seconds);
			
			long milliseconds = tempDateTime.until(toDateTime, ChronoUnit.MILLIS);
			
			dtt = dtt.minus(years, ChronoUnit.YEARS);
			dtt = dtt.minus(months, ChronoUnit.MONTHS);
			dtt = dtt.minus(days, ChronoUnit.DAYS);
			dtt = dtt.minus(hours, ChronoUnit.HOURS);
			dtt = dtt.minus(minutes, ChronoUnit.MINUTES);
			dtt = dtt.minus(seconds, ChronoUnit.SECONDS);
			dtt = dtt.minus(milliseconds, ChronoUnit.MILLIS);
			
			System.out.println(dtt);

			if (dtt.isAfter(dt2)) {
				
				JOptionPane.showMessageDialog(null, "Starting snipe...", "Currently sniping: " + tf.getText(),
						JOptionPane.INFORMATION_MESSAGE);
				while (dtt.isAfter(dt2)) {

					dt2 = LocalDateTime.now();
				
				}

				l.setText("Info: Attempting Snipe...");
				JOptionPane.showMessageDialog(null, "Sending POST request to Mojang API...", "Making POST Request...",
						JOptionPane.INFORMATION_MESSAGE);

				for (int i = 0; i < 20; i++) {
					MakeJSONRequest(u, p, u2, at);
				}
			} else {
				l.setText("Info: That time has already passed");
				
				JOptionPane.showMessageDialog(null, "That time has passed", "Error: Time has passed.",
						JOptionPane.ERROR_MESSAGE);
			}
		} catch (Exception e1) {
			l.setText("Info: Invaild Date or network unreacable");
			JOptionPane.showMessageDialog(null, "That time is Invaild. Please check to see if you're connected to the internet!", "Error: Time is Invaild.",
					JOptionPane.ERROR_MESSAGE);
			
			System.out.println(e1);
		}
	}

	public static void MakeJSONRequest(String username, String password, String uuid, String authToken) {
		l.setText("Info: Making request...");
		
		try {
			@SuppressWarnings("unused")
			HttpResponse<String> response = Unirest.post("https://api.mojang.com/user/profile/" + uuid + "/name")
					.header("content-type", "application/json").header("authorization", "" + authToken + "")
					.body("{\"name\":\"" + username + "\",\"password\":\"" + password + "\"}").asString();
			l.setText("Info: Request successful.");
		} catch (UnirestException e) {
			l.setText("Info: Request faied.");
			e.printStackTrace();
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
		long sum = 0;

		for (int i = 0; i < 10; i++) {
			avgping[i] = getPing("api.mojang.com");
		}

		for (int i = 0; i < avgping.length; i++) {
			sum += avgping[i];
		}

		pinglatency = sum / avgping.length;
		
		pl.setText("Ping: " + pinglatency);
		
		String username = tf.getText();
		@SuppressWarnings("deprecation")
		String password = tf2.getText();
		String date = tf5.getText();
		String email = tf4.getText();

		String authToken = "Bearer " + getAuthCode(email, password);
		String uuid = getUUID(email, password);

		System.out.println(authToken);
		System.out.println(uuid);

		//if (uuid != "" && authToken != "Bearer ") {
			startSnipe(username, password, uuid, authToken, date);
		//}
	}
	
	public static void setupGUI() {
		Image icon = null;
		Image icon2 = null;
		
		try {
			URL url = new URL("https://static.thenounproject.com/png/70816-200.png");
			icon = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			URL url = new URL("https://cdn2.iconfinder.com/data/icons/font-awesome/1792/code-512.png");
			icon2 = ImageIO.read(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JFrame f = new JFrame();
		f.setSize(840, 420);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setTitle("ESP: Een's Sniping Program");
		f.setIconImage(icon2);
		f.setResizable(false);

		JPanel p = new JPanel();
		f.getContentPane().add(p);

		p.setLayout(null);
		
		l = new JLabel("Info: Idle...");
		l.setBounds(10, 20, 420, 25);
		p.add(l);
		
		JLabel cl = new JLabel("𝙀𝙎𝙋: 𝙀𝙚𝙣'𝙨 𝙎𝙣𝙞𝙥𝙞𝙣𝙜 𝙋𝙧𝙤𝙜𝙧𝙖𝙢");
		cl.setBounds(460, 225, 420, 45);
		cl.setFont(new Font("Serif", Font.PLAIN, 20));
		p.add(cl);
		
		JLabel cl2 = new JLabel("Copyright © 2020 EenDevCo.");
		cl2.setBounds(7, 355, 420, 45);
		p.add(cl2);
		
		pl = new JLabel("Ping: " + pinglatency);
		pl.setBounds(10, 40, 420, 25);
		p.add(pl);

		JLabel l2 = new JLabel("Wanted Username:");
		l2.setBounds(10, 80, 160, 25);
		p.add(l2);

		tf = new JTextField(10);
		tf.setBounds(125, 80, 165, 25);
		p.add(tf);

		JLabel l3 = new JLabel("Account Password:");
		l3.setBounds(10, 140, 160, 25);
		p.add(l3);

		tf2 = new JPasswordField(10);
		tf2.setBounds(125, 140, 165, 25);
		p.add(tf2);

		JLabel l5 = new JLabel("Account Email:");
		l5.setBounds(10, 110, 160, 25);
		p.add(l5);

		tf4 = new JTextField(10);
		tf4.setBounds(125, 110, 165, 25);
		p.add(tf4);

		JLabel l6 = new JLabel("Date (24 hr) (08/25/2020 09:46:41):");
		l6.setBounds(10, 200, 225, 25);
		p.add(l6);

		tf5 = new JTextField(10);
		tf5.setBounds(215, 200, 165, 25);
		p.add(tf5);

		JButton b = new JButton("Start");
		b.setBounds(10, 270, 280, 25);
		p.add(b);
		
		JLabel p1 = new JLabel(new ImageIcon(icon));
		p1.setBounds(335, -105, 512, 512);
		p.add(p1);
		
		b.addActionListener(new SnipingProgram());

		f.setVisible(true);
	}

	static long getPing(String hostAddress) {
		InetAddress inetAddress = null;
		Date start, stop;

		try {
			inetAddress = InetAddress.getByName(hostAddress);
		} catch (UnknownHostException e) {
			System.out.println("Problem, unknown host:");
			e.printStackTrace();
		}

		try {
			start = new Date();
			if (inetAddress.isReachable(5000)) {
				stop = new Date();
				return (stop.getTime() - start.getTime());
			}

		} catch (IOException e1) {
			System.out.println("Problem, a network error has occurred:");
			e1.printStackTrace();
		} catch (IllegalArgumentException e1) {
			System.out.println("Problem, timeout was invalid:");
			e1.printStackTrace();
		}

		return -1; // to indicate failure

	}

	public static String getUUID(String e, String p) {
		String uuid = "";

		try {
			HttpResponse<JsonNode> response = Unirest.post("https://authserver.mojang.com/authenticate")
					.header("content-type", "application/json")
					.body("{\n    \"username\": \"" + e + "\",     \n                        \n    \"password\": \"" + p
							+ "\",\n     \n    \"requestUser\": true               \n}")
					.asJson();

			uuid = response.getBody().getObject().getJSONObject("user").getString("id");
		} catch (UnirestException | JSONException e1) {
			l.setText("Info: Invaild email or password");
			JOptionPane.showMessageDialog(null, "Password or Email is invaild.", "Error: Invaild Email + Password.",
					JOptionPane.ERROR_MESSAGE);
		}

		return uuid;
	}

	public static String getAuthCode(String email, String password) {
		String authToken = "";

		try {

			HttpResponse<JsonNode> response = Unirest.post("https://authserver.mojang.com/authenticate")
					.header("content-type", "application/json")
					.body("{\n    \"username\": \"" + email + "\",     \n                        \n    \"password\": \""
							+ password + "\",\n     \n    \"requestUser\": true               \n}")
					.asJson();
			authToken = response.getBody().getObject().getString("accessToken");
		} catch (JSONException | UnirestException e) {
			l.setText("Info: Invaild email or password");
		}

		return authToken;
	}
}