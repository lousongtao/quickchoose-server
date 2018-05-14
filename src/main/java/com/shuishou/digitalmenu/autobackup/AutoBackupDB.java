package com.shuishou.digitalmenu.autobackup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.shuishou.digitalmenu.ConstantValue;

/**
 * Tomcat 启动时备份一次完整数据库, 然后设定每小时备份一次重要数据, 比如会员; 每天备份一次整库;
 * 比较已备份的数据, 超过某一定时限的备份数据自动删除. 目前保留两周数据
 * 备份数据自动压缩.
 * @author Administrator
 *
 */
@Component
public class AutoBackupDB implements InitializingBean{
	private final static Logger logger = LoggerFactory.getLogger(AutoBackupDB.class);
	private int memberTimerDelay = 60 * 60 * 1000;
	private int memberTimerRepeat = 60 * 60 * 1000;
	private int logKeepDays = 15;
	
	private String configFile = "/server_config.properties";
	
	public static Properties prop = new Properties();
	private String mysqlDirectory;
	private String username = "root";
	private String password = "root";
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
		path = path.substring(1);//remove the first char '/'
		final String dbdirPath = path + "../../../" + ConstantValue.CATEGORY_BACKUPDB;
		File dbdir = new File(dbdirPath);
		if (!dbdir.exists()){
			dbdir.mkdirs();
		}
		
		//检查旧的备份文件, 删除两周以前的
		deleteOldFile(dbdir);
		
		readConfig();
		
		//系统启动时, 备份数据库, 全库
		mysqlDirectory = prop.getProperty("MySQLDirectory") + "\\bin";
		String dbfileWhole = dbdirPath + "/" +ConstantValue.DFYMDHMS_2.format(new Date()) + "-Whole.sql";
		String dumpCmd = null;
		String osname = System.getProperty("os.name");
		if (osname.toLowerCase().startsWith("windows")){
			dumpCmd = "cmd.exe /c " + mysqlDirectory + "\\mysqldump";
		} else if (osname.toLowerCase().startsWith("mac")) {
			dumpCmd = "/bin/sh -c " + mysqlDirectory + "\\mysqldump";
		} else if (osname.toLowerCase().startsWith("linux")){
			dumpCmd = "/bin/sh -c " + mysqlDirectory + "\\mysqldump";
		}
		final String dumpCommand = dumpCmd;
		String dumpParam = " -u"+username+" -p"+password+" --databases digitalmenu > " + dbfileWhole;
		
		logger.debug("backup whole database : "+ dumpCommand + dumpParam);
		Runtime runtime = Runtime.getRuntime();
		runtime.exec(dumpCommand + dumpParam);
		
		
		//启动定时器, 定期备份会员数据
		Timer timerMember = new Timer();
		timerMember.schedule(new TimerTask(){

			@Override
			public void run() {
				//dump member data
				String dbfile = dbdirPath + "/" + ConstantValue.DFYMDHMS_2.format(new Date()) + "-Member.sql";
				String dumpMemberParam = " -u"+username+" -p"+password+" digitalmenu member > " + dbfile;
				logger.debug("backup member data : "+ dumpCommand + dumpMemberParam);
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec(dumpCommand + dumpMemberParam);
				} catch (IOException e) {
					logger.error("", e);
				}
			}
			
		}, memberTimerDelay, memberTimerRepeat);
		
		//定义一个整库备份的计时器, 每天凌晨12点后开始备份; 由于前面已经备份了一次整库, 所以这里只是为个别晚上不关机的商家设计, 对于晚上会关机的商家, 这个地方应用不上
		//根据当前时间, 计算下一天2点离现在的时间, 即为delay time
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		Timer timerWhole = new Timer();
		timerWhole.schedule(new TimerTask(){

			@Override
			public void run() {
				String dbfile = dbdirPath + "/" +ConstantValue.DFYMDHMS_2.format(new Date()) + "-Whole.sql";
				String dumpParam = " -u"+username+" -p"+password+" --databases digitalmenu > " + dbfile;
				logger.debug("backup whole database : "+ dumpCommand + dumpParam);
				Runtime runtime = Runtime.getRuntime();
				try {
					runtime.exec(dumpCommand + dumpParam);
				} catch (IOException e) {
					logger.error("", e);
				}
			}}, (25-hour) * 60 * 60 * 1000, //不确定具体时间, 只要凌晨后即可, 所以这里只考虑小时的间隔 
				24 * 60 * 60 * 1000); //重复时间为24小时
	}
	
	private void readConfig(){
		InputStream input = null;
		try {
			input = this.getClass().getClassLoader().getResourceAsStream(configFile);
			prop.load(input);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	//删除过老的文件
	private void deleteOldFile(File directory){
		File[] dbfiles = directory.listFiles();
		if (dbfiles != null && dbfiles.length > 0){
			for (File file : dbfiles){
				String filename = file.getName();
				//file name like 20180511020304-***.sql
				String[] stimes = filename.split("-");
				if (stimes.length < 2)
					continue;//unrecognized file
				String timename = filename.split("-")[0];
				try {
					Date filetime = ConstantValue.DFYMDHMS_2.parse(timename);
					if ((new Date().getTime() - filetime.getTime()) / (24*60*60*1000) > logKeepDays){
						file.delete();
					}
				} catch (ParseException e) {
					logger.error("", e);
				}
			}
		}
	}
}
