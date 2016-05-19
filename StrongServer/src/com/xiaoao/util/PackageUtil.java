package com.xiaoao.util;

/**
 * @author <a href="mailto:cliff7777@gmail.com">cliff</a>
 * @since 2014-8-15
 */
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PackageUtil {

    /**
     * 获取某包下（包括该包的所有子包）所有类
     *
     * @param packageName 包名 类的完整名称
     */
    public static List<String> getClassName(String packageName) {
        return getClassName(packageName, true);
    }

    /**
     * 获取某包下所有类
     *
     * @param packageName 包名
     * @param childPackage 是否遍历子包 类的完整名称
     */
    public static List<String> getClassName(String packageName, boolean childPackage) {
        List<String> fileNames = null;
//        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ClassLoader loader = PackageUtil.class.getClassLoader();
        String packagePath = packageName.replace(".", "/");
//        URL url = loader.getResource(packagePath);
//        ClassLoader loader = ClassLoader.getSystemClassLoader();
//        URL url = loader.getResource(packagePath);
        URL url = loader.getResource(packagePath);
        if (url != null) {
            String type = url.getProtocol();
            if ("file".equals(type)) {
                fileNames = getClassNameByFile(url.getPath(), childPackage);
            } else if ("jar".equals(type)) {
                fileNames = getClassNameByJar(url.getPath(), childPackage);
            }
        } else {
            fileNames = getClassNameByJars(((URLClassLoader) loader).getURLs(), packagePath, childPackage);
        }
        return fileNames;
    }

    /**
     * 从项目文件获取某包下所有类
     *
     * @param filePath 文件路径
     * @param className 类名集合
     * @param childPackage 是否遍历子包 类的完整名称
     */
    private static List<String> getClassNameByFile(String filePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>(500);
        File file = new File(filePath);
        File[] childFiles = file.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isDirectory()) {
                if (childPackage) {
                    myClassName.addAll(getClassNameByFile(childFile.getPath(), childPackage));
                }
            } else {
                String childFilePath = childFile.getPath();
                
                if (childFilePath.endsWith(".class") && childFilePath.indexOf('$') < 0) {
                	int classIndex = childFilePath.indexOf("\\classes");
                	if(classIndex == -1) {
                		classIndex = childFilePath.indexOf("/classes");
                		childFilePath = childFilePath.substring(classIndex + 9, childFilePath.lastIndexOf('.'));
                		childFilePath = childFilePath.replace("/", ".");
                	}else{
                		childFilePath = childFilePath.substring(classIndex + 9, childFilePath.lastIndexOf('.'));
                		childFilePath = childFilePath.replace("\\", ".");
                	}
                    myClassName.add(childFilePath);
                }
            }
        }
        return myClassName;
    }

    /**
     * 从jar获取某包下所有类
     *
     * @param jarPath jar文件路径
     * @param childPackage 是否遍历子包 类的完整名称
     */
    private static List<String> getClassNameByJar(String jarPath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>(500);
        String[] jarInfo = jarPath.split("!");
        String jarFilePath = jarInfo[0].substring(jarInfo[0].indexOf('/'));
        String packagePath = jarInfo[1].substring(1);
        JarFile jarFile = null;
        try {
            jarFile = new JarFile(jarFilePath);
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (entryName.endsWith(".class")&&entryName.indexOf('$')<0) {
                    if (childPackage) {
                        if (entryName.startsWith(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf('.'));
                            myClassName.add(entryName);
                        }
                    } else {
                        int index = entryName.lastIndexOf('/');
                        String myPackagePath;
                        if (index != -1) {
                            myPackagePath = entryName.substring(0, index);
                        } else {
                            myPackagePath = entryName;
                        }
                        if (myPackagePath.equals(packagePath)) {
                            entryName = entryName.replace("/", ".").substring(0, entryName.lastIndexOf('.'));
                            myClassName.add(entryName);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(PackageUtil.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (jarFile != null) {
                try {
                    jarFile.close();
                } catch (IOException ex) {
                    Logger.getLogger(PackageUtil.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return myClassName;
    }

    /**
     * 从所有jar中搜索该包，并获取该包下所有类
     *
     * @param urls URL集合
     * @param packagePath 包路径
     * @param childPackage 是否遍历子包 类的完整名称
     */
    private static List<String> getClassNameByJars(URL[] urls, String packagePath, boolean childPackage) {
        List<String> myClassName = new ArrayList<String>(500);
        if (urls != null) {
            for (URL url : urls) {
                String urlPath = url.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                String jarPath = urlPath + "!/" + packagePath;
                myClassName.addAll(getClassNameByJar(jarPath, childPackage));
            }
        }
        return myClassName;
    }

    private PackageUtil() {
    }
}
