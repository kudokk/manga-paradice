USE compass_master_db;
SET FOREIGN_KEY_CHECKS=0;

CREATE TABLE IF NOT EXISTS `time_targeting` (
    `time_targeting_id` MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT,
    `co_account_id` SMALLINT UNSIGNED NOT NULL,
    `time_targeting_name` VARCHAR(255) NOT NULL,
    `time_targeting_status` ENUM('active', 'archive', 'deleted') NOT NULL DEFAULT 'active',
    `country_id` SMALLINT unsigned NULL DEFAULT NULL,
    `is_active_holiday` ENUM('true', 'false') NOT NULL DEFAULT 'false',
    `description` VARCHAR(1023) NULL DEFAULT NULL,
    `update_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`time_targeting_id`)
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS `time_targeting_day_type_period` (
    `time_targeting_id` MEDIUMINT UNSIGNED NOT NULL,
    `day_type` ENUM('mon', 'tue', 'wed', 'thu', 'fri', 'sat', 'sun', 'hol') NOT NULL,
    `start_time` TIME NOT NULL DEFAULT '00:00:00',
    `end_time` TIME NOT NULL DEFAULT '23:59:00',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`time_targeting_id`, `day_type`, `start_time`),
    FOREIGN KEY (`time_targeting_id`)
    REFERENCES `time_targeting` (`time_targeting_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE = InnoDB DEFAULT CHARSET=utf8;

/* プロシージャ */
DROP PROCEDURE IF EXISTS alter_table_procedure;

DELIMITER //

CREATE PROCEDURE alter_table_procedure()
BEGIN
    /* SQL EXCEPTIONを無視するように設定 */
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION BEGIN END;

    /* 以下のALTER TABLEで『Duplicate column name』エラーが発生してもプロシージャは正常終了する */
    ALTER TABLE compass_struct
        ADD COLUMN time_targeting_id MEDIUMINT UNSIGNED NULL DEFAULT NULL AFTER reseller_flag;
END //

DELIMITER ;

CALL alter_table_procedure();

DROP PROCEDURE alter_table_procedure;

/* 追加対象のカラムをSELECTすることで、もしカラム追加できていなかったらエラーとなる */
SELECT time_targeting_id FROM compass_struct limit 0;
