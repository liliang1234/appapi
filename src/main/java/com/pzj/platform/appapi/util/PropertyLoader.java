package com.pzj.platform.appapi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class PropertyLoader {

    private static final Timer  TimerFactory         = new Timer();
    private static final String PROPERTY_FILE_SUFFIX = ".properties";

    private Map<String, Long> read_time = new ConcurrentHashMap<String, Long>();

    private Map<String, Map<String, String>> propFileMap = new ConcurrentHashMap<String, Map<String, String>>();

    private String       include_dir;
    private List<String> exclude_file_list;
    private List<String> lazy_file_list;
    private int          schedule_timer = 60;

    public String getInclude_dir() {
        return include_dir;
    }

    public void setInclude_dir(String include_dir) {
        this.include_dir = include_dir;
    }

    public List<String> getExclude_file_list() {
        return exclude_file_list;
    }

    public void setExclude_file_list(List<String> exclude_file_list) {
        this.exclude_file_list = exclude_file_list;
    }

    public List<String> getLazy_file_list() {
        return lazy_file_list;
    }

    public void setLazy_file_list(List<String> lazy_file_list) {
        this.lazy_file_list = lazy_file_list;
    }

    public int getSchedule_timer() {
        return schedule_timer;
    }

    public void setSchedule_timer(int schedule_timer) {
        this.schedule_timer = schedule_timer;
    }

    public PropertyLoader() {
        super();
    }

    public PropertyLoader(String include_dir, List<String> exclude_file_list, List<String> lazy_file_list, int schedule_timer) {
        super();
        this.include_dir = include_dir;
        this.exclude_file_list = exclude_file_list;
        this.lazy_file_list = lazy_file_list;
        this.schedule_timer = 60;
        initProperties();
        TimerFactory.scheduleAtFixedRate(new PropertyGuardDaemon(), TimeUnit.MINUTES.toMillis(1), this.schedule_timer * TimeUnit.SECONDS.toMillis(1));

    }

    private void initProperties() {
        String classpath = getPropertiesDir(include_dir);
        File f = new File(classpath);
        if (f == null || !f.exists() || !f.isDirectory())
            return;
        File[] fileList = f.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(PROPERTY_FILE_SUFFIX) && !exclude_file_list.contains(name);
            }
        });
        if (fileList == null || fileList.length == 0)
            return;
        for (File propFile : fileList) {
            Long lastTime = read_time.get(propFile.getName());
            Long lastModifyTime = propFile.lastModified();
            if (lastTime != null && lastTime >= lastModifyTime)
                continue;

            if (lazy_file_list.contains(propFile.getName())) {
                Long lastLazyTime = read_time.get(propFile.getName());
                if (lastLazyTime != null && lastLazyTime < lastModifyTime) {
                    System.out.println("lazy file " + propFile.getName() + " modified, will remove it from cache.");
                    propFileMap.remove(propFile.getName());
                }
                continue;
            }
            try {
                loadProperties(propFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadProperties(File f) throws FileNotFoundException, IOException {
        System.out.println("will load file:" + f.getAbsolutePath());
        if (f == null || !f.exists())
            return;
        String filename = f.getName();
        if (propFileMap.containsKey(filename))
            propFileMap.remove(filename);
        Map<String, String> propMap = new HashMap<String, String>();
        Properties p = new Properties();
        p.load(new InputStreamReader(new FileInputStream(f), "utf-8"));
        Iterator<Entry<Object, Object>> iter = p.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<Object, Object> entry = iter.next();
            String key = entry.getKey().toString();
            String value = entry.getValue().toString();
            propMap.put(key, value);
        }
        propFileMap.put(filename, propMap);
        read_time.put(filename, f.lastModified());
        System.out.println("load include file:" + f.getAbsolutePath() + " ok!");
    }

    public String getProperty(String filename, String key) {
        if (!filename.endsWith(PROPERTY_FILE_SUFFIX))
            filename = filename.concat(PROPERTY_FILE_SUFFIX);
        if (propFileMap.containsKey(filename)) {
            return propFileMap.get(filename).get(key);
        }
        if (lazy_file_list.contains(filename)) {
            try {
                loadProperties(new File(getPropertiesDir(include_dir + filename)));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return propFileMap.get(filename).get(key);
    }

    public String getProperty(String filename, String key, String defaultValue) {
        String value = getProperty(filename, key);
        return (value == null ? defaultValue : value);
    }

    private static final String getPropertiesDir(String propDir) {
        //		 System.out.println(propDir + "=================");
        //		 System.out.println(PropertyLoader.class.getClassLoader()
        //		 .getResource(propDir).getPath()
        //		 + "=================");
        return PropertyLoader.class.getClassLoader().getResource(propDir).getPath();

    }

    private class PropertyGuardDaemon extends TimerTask {

        @Override
        public void run() {
            initProperties();
        }
    }

    public static void main(String[] args) {
        String filename = "/D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/pzj2_20141013/WEB-INF/classes/";
        String filename2 = "D:/workspace/.metadata/.plugins/org.eclipse.wst.server.core/tmp1/wtpwebapps/pzj2_20141013/WEB-INF/classes/";
        File f1 = new File(filename);
        File f2 = new File(filename2);
        System.out.println(f1.exists());
        System.out.println(f2.exists());

    }
}
