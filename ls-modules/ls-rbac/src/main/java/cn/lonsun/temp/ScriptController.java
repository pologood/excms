package cn.lonsun.temp;

import java.io.FileReader;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptController {

	public static void main(String[] args) throws Exception {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("javascript");
		String jsFileName = "src/main/webapp/app/test.js"; // 读取js文件
		FileReader reader = new FileReader(jsFileName); // 执行指定脚本
		engine.eval(reader);
		if (engine instanceof Invocable) {
			Invocable invoke = (Invocable) engine;
			String jsMethodName = "init";
			String message = (String)invoke.invokeFunction(jsMethodName, "hello");// 调用merge方法，并传入两个参数
			System.out.println(message);
		}
		reader.close();
	}

}
