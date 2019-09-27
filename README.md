# ДЗ03 - DSL for TeX

**Дедлайн**: 05.10.2019 23:59 (полный балл)

Необходимо реализовать простую библиотеку с DSL для верстки в TeX.

## Требования
- Должна быть возможность создавать строку из DSL-представления или выводить его в OutputStream
- По возможности нужно избегать хранения всего дерева (сразу выводить в OutputStream, например)
- Ожидается поддержка стандартных тегов/комманд, используемых при верстке презентации
    - document, documentclass, usepackage
    - frame, itemize, enumerate, math, alignment
    - возможность добавления кастомных тегов
- Воспользуйтесь `DslMarker`, чтобы `item` нельзя было использовать вне `itemize`/`enumerate`
- Тесты, конечно же, нужны. Не забываем про [`trimMargin`](http://kotlinlang.org/api/latest/jvm/stdlib/kotlin.text/trim-margin.html)
- Перед выполнением рекомендуется посмотреть пример с [DSL для html](https://kotlinlang.org/docs/reference/type-safe-builders.html)
- Возможность генерировать DSL в pdf приветствуется :)

## Пример
```kotlin
document {
    documentClass("beamer")
    usepackage("babel", "russian" /* varargs */)
    frame(frameTitle="frametitle", "arg1" to "arg2") {
        itemize {
            for (row in rows) {
                item { + "$row text" }
            }
        }

        // begin{pyglist}[language=kotlin]...\end{pyglist}
        customTag(name = "pyglist", "language" to "kotlin") {
            +"""
               |val a = 1
               |
            """
        }
    }
}.toOutputStream(System.out)
```
