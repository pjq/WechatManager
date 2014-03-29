package me.pjq.wechat;

import me.pjq.wechat.service.DictionService;
import cjc.weixinmp.AbstractWeixinmpController;
import cjc.weixinmp.bean.TextRequest;

public class CommandsHelper {
	private static CommandsHelper INSTANCE;
	AbstractWeixinmpController controller;

	public final void setController(AbstractWeixinmpController controller) {
		if (this.controller != controller) {
			this.controller = controller;
		}
	}

	public static CommandsHelper getInstance(
			AbstractWeixinmpController controller) {
		if (null == INSTANCE) {
			INSTANCE = new CommandsHelper();
			INSTANCE.setController(controller);
		}

		return INSTANCE;
	}

	public String commandParser(TextRequest text) {
		String command = text.Content;
		String help = controller
				.findHelp("已经接收到你的消息, 不要再调戏我了, 我正在苦逼的开发新功能中...更多消息请联系我的主人JianqingPeng.\n请输入?获取更多的信息");

		if (command.equalsIgnoreCase(Constants.COMMAND_HELP)) {
			help = controller.findHelp("command_list");
		} else if (command.startsWith(Constants.COMMAND_ABOUT)) {
			help = controller.findHelp("about");
		} else if (command.startsWith(Constants.COMMAND_PM25)) {
			help = controller.findHelp("feature_ongoing");
		} else if (command.startsWith(Constants.COMMAND_WEATHER)) {
			help = controller.findHelp("feature_ongoing");
		} else if (command.startsWith(Constants.COMMAND_CHAI)) {
			//help = controller.findHelp("feature_ongoing");
			int beginIndex = Constants.COMMAND_CHAI.length();
			String input = command.substring(beginIndex + 1);
			help = DictionService.getInstance().convert(input);
		} else {

		}

		return help;
	}
}
