package com.example.sumcalculator

class Expression() {
    private var parent: Expression? = null

    // if not null, overrides values of first and second expression
    private var intValue: Int? = null
    private var oper: String? = null

    private var firstExpr: Expression? = null
    private var secondExpr: Expression? = null

    constructor(firstValue: Int, oper: String? = null): this() {
        this.oper = oper

        if (oper != null)
            firstExpr = Expression(this, firstValue)
        else
            intValue = firstValue
    }

    constructor(firstValue: Expression, oper: String): this() {
        this.oper = oper
        firstValue.parent = this
        firstExpr = firstValue
    }

    private constructor(parent: Expression, firstValue: Int, operStr: String? = null) : this(firstValue, operStr) {
        this.parent = parent
    }
    private constructor(parent: Expression, firstValue: Expression, operStr: String): this(firstValue, operStr) {
        this.parent = parent
    }

    private constructor(expr: Expression, parent: Expression): this() {
        this.parent = parent
        intValue = expr.intValue
        oper = expr.oper
        firstExpr = expr.firstExpr
        secondExpr = expr.secondExpr
    }

    fun append(secondValue: Int, newOper: String) = append(Expression(secondValue), newOper)
    fun append(secondValue: Expression, newOper: String) {
        when (newOper) {
            "+", "-" -> {
                if (firstExpr == null)
                    firstExpr = secondValue
                else {
                    firstExpr = Expression(this, this)
                    var lstExpr = firstExpr
                    while (lstExpr?.secondExpr != null) lstExpr = lstExpr.secondExpr
                    lstExpr?.secondExpr = secondValue
                    secondExpr = null
                }
                oper = newOper
            }
            "*" -> {
                when {
                    firstExpr == null -> {
                        firstExpr = secondValue
                        oper = newOper
                    }

                    secondExpr == null -> secondExpr = Expression(this, secondValue, newOper)

                    else -> {
                        var lstExpr = secondExpr!!
                        while (lstExpr.secondExpr != null) lstExpr = lstExpr.secondExpr!!

                        lstExpr.secondExpr = Expression(lstExpr, secondValue, newOper)
                    }
                }
            }
            "=" -> {
                var lstExpr = this
                while (lstExpr.secondExpr != null) lstExpr = lstExpr.secondExpr!!

                secondValue.parent = lstExpr
                lstExpr.secondExpr = secondValue
            }
        }
    }

    fun solve(): Int? = intValue ?: run {
        //println(firstExpr)
        //println(secondExpr)
        val a = firstExpr?.solve() ?: return null
        val b = secondExpr?.solve() ?: return null
        //println(oper)
        when (oper) {
            "+" -> return a + b
            "-" -> return a - b
            "*" -> return a * b
        }
        return null
    }

    fun isEmpty(): Boolean = firstExpr == null
}