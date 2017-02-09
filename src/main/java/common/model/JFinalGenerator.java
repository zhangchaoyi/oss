package common.model;

import javax.sql.DataSource;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import common.config.BaseConfig;

public class JFinalGenerator {

	public static DataSource getDataSource() {
		C3p0Plugin c3p0Plugin = BaseConfig.createC3p0Plugin();
		c3p0Plugin.start();
		return c3p0Plugin.getDataSource();
	}

	public static void main(String[] args) {
		// base model 所使用的包名
		String baseModelPackageName = "common.model.base";
		// base model 文件保存路径
		String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/common/model/base";
		// model 所使用的包名 (MappingKit 默认使用的包名)
		String modelPackageName = "common.model";
		// model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
		String modelOutputDir = baseModelOutputDir + "/..";

		// 创建生成器
		Generator gernerator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir,
				modelPackageName, modelOutputDir);
		// 添加不需要生成的表名
		gernerator.addExcludedTable("arena_record");
		gernerator.addExcludedTable("arena_score");
		gernerator.addExcludedTable("guide_step");
		gernerator.addExcludedTable("hero_module_change");
		gernerator.addExcludedTable("ladder_record");
		gernerator.addExcludedTable("ladder_score");
		gernerator.addExcludedTable("log_battle");
		gernerator.addExcludedTable("log_order");
		gernerator.addExcludedTable("refresh_arena_exp_count");
		gernerator.addExcludedTable("client_view_hero_mp4");
		gernerator.addExcludedTable("game_gm_cmd");
		gernerator.addExcludedTable("eggs");
		
		// 设置是否在 Model 中生成 dao 对象
		gernerator.setGenerateDaoInModel(true);
		// 设置是否生成字典文件
		gernerator.setGenerateDataDictionary(false);
		// 设置需要被移除的表名前缀用于生成modelNaloginme。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为
		// "User"而非 OscUser
		// gernerator.setRemovedTableNamePrefixes("t_");
		// 生成
		gernerator.generate();
	}
}
