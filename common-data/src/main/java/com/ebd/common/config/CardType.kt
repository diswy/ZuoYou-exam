package com.ebd.common.config

enum class CardType(val value: Int) {
    SINGLE(1),/*单选题*/
    BLANK(2),/*填空题*/
    EDIT(3),/*解答题*/
    TRUE_OR_FALSE(4),/*判断题*/
    MULTI(5),/*多选题*/
    LISTEN_SINGLE(12)/*听力单选题*/,
    LISTEN_BLANK(13)/*听力填空题*/,
    EN_WORD(32),
    EN_SENTENCE(33),
    EN_PARAGRAPH(34),
    EN_FREE(35),
    EN_LISTEN_AND_READ(36),
    EN_LISTEN_AND_READ_SHOW(37),
    NONE(0);
}