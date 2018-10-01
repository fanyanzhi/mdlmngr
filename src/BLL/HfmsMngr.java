package BLL;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import net.cnki.hfs.FileClient;
import net.cnki.hfs.HFSInputStream;
import net.cnki.hfs.HFSOutputStream;
import net.cnki.hfs.HFS_OPEN_FILE;
import Util.Common;

public class HfmsMngr {

	private static FileClient fc = new FileClient(Common.GetConfig("HfsServer"));
	
	/**
	 * 向Hfms上传本地文件
	 * @param FileID
	 * @param FileType
	 * @param FilePath
	 * @return
	 */
	public static boolean uploadFile(String FileID, String FileType, String FilePath) { /* 上传文件的方法需要修改 */
		boolean bRet = true;
		String strLocalFilePath = UploadMngr.getTempFilePath(FilePath).concat(FileID).concat(".").concat(FileType);

		File f = new File(strLocalFilePath);
		if (!f.exists() || !f.canRead()) {
			Logger.WriteException(new Exception("本地文件打开失败,请检查文件路径."));
			return false;
		}

		String strRemoteRootPath = "cajcloud";
		if (Common.GetConfig("HfsFileFolder") != null) {
			strRemoteRootPath = Common.GetConfig("HfsFileFolder");
		}
		String strRemotePath = strRemoteRootPath.concat("\\").concat(FilePath);
		if (!createUploadDir(strRemotePath)) {
			Logger.WriteException(new Exception("创建Hmfs文件目录失败！"));
			return false;
		}
		String strRemoteFile = strRemotePath.concat("\\").concat(FileID).concat(".").concat(FileType);
		HFS_OPEN_FILE hof = null;
		try {
			hof = fc.OpenFile(strRemoteFile, "wb+");
		} catch (Exception e) {
			Logger.WriteException(new Exception("打开远程Hmfs文件失败：" + e.getMessage()));
			return false;
		}
		if (hof == null) {
			Logger.WriteException(new Exception("远程Hmfs文件句柄为NULL"));
			return false;
		}

		long lRet = hof.Handle;
		HFSOutputStream fileHandler = new HFSOutputStream(fc, lRet);

		long lSize = f.length();
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			Logger.WriteException(e);
			return false;
		}
		BufferedInputStream bin = new BufferedInputStream(fin);
		byte[] bytes = new byte[1024 * 64];
		int read = 0;
		int p = 0;
		try {
			while ((read = bin.read(bytes)) > 0) {
				fileHandler.write(bytes, read);
				p += read;
			}
			if (p < lSize)
				bRet = false;
			bin.close();
			fin.close();
			fileHandler.close();
			fc.CloseFile(lRet);
		} catch (Exception e) {
			Logger.WriteException(e);
			bRet = false;
		}
		return bRet;
	}
	
	/**
	 * 
	 * @param FileID
	 *            文件id。如OData上的期刊文件的id为：njyj200302093 个人上传的文件为32位的文件
	 * @param FileType
	 *            文件的类型，如pdf，epub and so on
	 * @param TempPath
	 *            文件的路径
	 * @return
	 */
	public static boolean downloadFile(String FileID, String FileType, String TempPath) {
		String strRemoteFilePath = HfmsMngr.getHfsFilePath(TempPath).concat(FileID).concat(".").concat(FileType);
		String strLocalPath = UploadMngr.getTempFilePath(TempPath).concat(FileID).concat(".").concat(FileType);
		HFS_OPEN_FILE hof = null;
		try {
			hof = fc.OpenFile(strRemoteFilePath, "rb");
		} catch (Exception e1) {
			Logger.WriteException(e1);
			return false;
		}
		if (hof == null) {
			Logger.WriteException(new Exception("远端文件打开失败,请检查文件路径或服务器状态."));
			return false;
		}
		long lRet = hof.Handle;
		long lSize = hof.File.FileSize;

		long handle = lRet;
		HFSInputStream in = new HFSInputStream(fc, handle);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(strLocalPath);
		} catch (FileNotFoundException e1) {
			Logger.WriteException(e1);
			return false;
		}
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		byte[] bytes = new byte[1024 * 64];// *255
		int read = 0;
		int p = 0;
		try {
			while ((read = in.read(bytes)) > 0) {
				bos.write(bytes, 0, read);
				p += read;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
		}
		try {
			bos.close();
			fos.close();
			in.close();
			fc.CloseFile(lRet);
			if (p < lSize) {
				return false;
			}
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		}
		return true;
	}

	public static String getHfsFilePath(String TempFilePath) {
		return Common.GetConfig("HfsFileFolder").concat("\\").concat(TempFilePath).concat("\\");
	}

	private static boolean createUploadDir(String RemotePath) {
		int isExist = 0;
		try {
			isExist = fc.IsDirExist(RemotePath);
		} catch (Exception e) {
			Logger.WriteException(e);
			return false;
		}
		if (isExist > 0) {
			return true;
		} else {
			try {
				if (fc.DirCreate(RemotePath) > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				Logger.WriteException(e);
				return false;
			}
		}
	}

}
