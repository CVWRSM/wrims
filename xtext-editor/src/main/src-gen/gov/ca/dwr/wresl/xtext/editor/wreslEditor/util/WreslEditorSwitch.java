/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.util;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.*;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.util.Switch;

/**
 * <!-- begin-user-doc -->
 * The <b>Switch</b> for the model's inheritance hierarchy.
 * It supports the call {@link #doSwitch(EObject) doSwitch(object)}
 * to invoke the <code>caseXXX</code> method for each class of the model,
 * starting with the actual class of the object
 * and proceeding up the inheritance hierarchy
 * until a non-null result is returned,
 * which is the result of the switch.
 * <!-- end-user-doc -->
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage
 * @generated
 */
public class WreslEditorSwitch<T> extends Switch<T>
{
  /**
   * The cached model package
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static WreslEditorPackage modelPackage;

  /**
   * Creates an instance of the switch.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEditorSwitch()
  {
    if (modelPackage == null)
    {
      modelPackage = WreslEditorPackage.eINSTANCE;
    }
  }

  /**
   * Checks whether this is a switch for the given package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @parameter ePackage the package in question.
   * @return whether this is a switch for the given package.
   * @generated
   */
  @Override
  protected boolean isSwitchFor(EPackage ePackage)
  {
    return ePackage == modelPackage;
  }

  /**
   * Calls <code>caseXXX</code> for each class of the model until one returns a non null result; it yields that result.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @return the first non-null result returned by a <code>caseXXX</code> call.
   * @generated
   */
  @Override
  protected T doSwitch(int classifierID, EObject theEObject)
  {
    switch (classifierID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR:
      {
        WreslEvaluator wreslEvaluator = (WreslEvaluator)theEObject;
        T result = caseWreslEvaluator(wreslEvaluator);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.PATTERN:
      {
        Pattern pattern = (Pattern)theEObject;
        T result = casePattern(pattern);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DECLARATION:
      {
        Declaration declaration = (Declaration)theEObject;
        T result = caseDeclaration(declaration);
        if (result == null) result = caseWreslEvaluator(declaration);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VARIABLE:
      {
        Variable variable = (Variable)theEObject;
        T result = caseVariable(variable);
        if (result == null) result = casePattern(variable);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.STATE_VARIABLE:
      {
        StateVariable stateVariable = (StateVariable)theEObject;
        T result = caseStateVariable(stateVariable);
        if (result == null) result = caseVariable(stateVariable);
        if (result == null) result = casePattern(stateVariable);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DECISION_VARIABLE:
      {
        DecisionVariable decisionVariable = (DecisionVariable)theEObject;
        T result = caseDecisionVariable(decisionVariable);
        if (result == null) result = caseVariable(decisionVariable);
        if (result == null) result = casePattern(decisionVariable);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.IF_INC_ITEMS:
      {
        IfIncItems ifIncItems = (IfIncItems)theEObject;
        T result = caseIfIncItems(ifIncItems);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.IF_TERM:
      {
        IfTerm ifTerm = (IfTerm)theEObject;
        T result = caseIfTerm(ifTerm);
        if (result == null) result = caseIfIncItems(ifTerm);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ELSE_IF_TERM:
      {
        ElseIfTerm elseIfTerm = (ElseIfTerm)theEObject;
        T result = caseElseIfTerm(elseIfTerm);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ELSE_TERM:
      {
        ElseTerm elseTerm = (ElseTerm)theEObject;
        T result = caseElseTerm(elseTerm);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TIME_ARRAY_SIZE:
      {
        TimeArraySize timeArraySize = (TimeArraySize)theEObject;
        T result = caseTimeArraySize(timeArraySize);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.OBJECTIVE:
      {
        Objective objective = (Objective)theEObject;
        T result = caseObjective(objective);
        if (result == null) result = casePattern(objective);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.WEIGHT_ITEM:
      {
        WeightItem weightItem = (WeightItem)theEObject;
        T result = caseWeightItem(weightItem);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.EXTERNAL_DEF:
      {
        ExternalDef externalDef = (ExternalDef)theEObject;
        T result = caseExternalDef(externalDef);
        if (result == null) result = caseVariable(externalDef);
        if (result == null) result = casePattern(externalDef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_DEF:
      {
        SvarDef svarDef = (SvarDef)theEObject;
        T result = caseSvarDef(svarDef);
        if (result == null) result = caseStateVariable(svarDef);
        if (result == null) result = caseVariable(svarDef);
        if (result == null) result = casePattern(svarDef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_DEF:
      {
        DvarDef dvarDef = (DvarDef)theEObject;
        T result = caseDvarDef(dvarDef);
        if (result == null) result = caseDecisionVariable(dvarDef);
        if (result == null) result = caseVariable(dvarDef);
        if (result == null) result = casePattern(dvarDef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CONST_DEF:
      {
        ConstDef constDef = (ConstDef)theEObject;
        T result = caseConstDef(constDef);
        if (result == null) result = caseStateVariable(constDef);
        if (result == null) result = caseVariable(constDef);
        if (result == null) result = casePattern(constDef);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ALIAS:
      {
        Alias alias = (Alias)theEObject;
        T result = caseAlias(alias);
        if (result == null) result = caseDecisionVariable(alias);
        if (result == null) result = caseVariable(alias);
        if (result == null) result = casePattern(alias);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.EXTERNAL:
      {
        External external = (External)theEObject;
        T result = caseExternal(external);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR:
      {
        DVar dVar = (DVar)theEObject;
        T result = caseDVar(dVar);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_NON_STD:
      {
        DVarNonStd dVarNonStd = (DVarNonStd)theEObject;
        T result = caseDVarNonStd(dVarNonStd);
        if (result == null) result = caseDVar(dVarNonStd);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_STD:
      {
        DVarStd dVarStd = (DVarStd)theEObject;
        T result = caseDVarStd(dVarStd);
        if (result == null) result = caseDVar(dVarStd);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_INTEGER:
      {
        DVarInteger dVarInteger = (DVarInteger)theEObject;
        T result = caseDVarInteger(dVarInteger);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_INTEGER_STD:
      {
        DVarIntegerStd dVarIntegerStd = (DVarIntegerStd)theEObject;
        T result = caseDVarIntegerStd(dVarIntegerStd);
        if (result == null) result = caseDVarInteger(dVarIntegerStd);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.DVAR_INTEGER_NON_STD:
      {
        DVarIntegerNonStd dVarIntegerNonStd = (DVarIntegerNonStd)theEObject;
        T result = caseDVarIntegerNonStd(dVarIntegerNonStd);
        if (result == null) result = caseDVarInteger(dVarIntegerNonStd);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR:
      {
        SVar sVar = (SVar)theEObject;
        T result = caseSVar(sVar);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_DSS:
      {
        SVarDSS sVarDSS = (SVarDSS)theEObject;
        T result = caseSVarDSS(sVarDSS);
        if (result == null) result = caseSVar(sVarDSS);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_EXPRESSION:
      {
        SVarExpression sVarExpression = (SVarExpression)theEObject;
        T result = caseSVarExpression(sVarExpression);
        if (result == null) result = caseSVar(sVarExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_SUM:
      {
        SVarSum sVarSum = (SVarSum)theEObject;
        T result = caseSVarSum(sVarSum);
        if (result == null) result = caseSVar(sVarSum);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_TABLE:
      {
        SVarTable sVarTable = (SVarTable)theEObject;
        T result = caseSVarTable(sVarTable);
        if (result == null) result = caseSVar(sVarTable);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SVAR_CASE:
      {
        SVarCase sVarCase = (SVarCase)theEObject;
        T result = caseSVarCase(sVarCase);
        if (result == null) result = caseSVar(sVarCase);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CASE_CONTENT:
      {
        CaseContent caseContent = (CaseContent)theEObject;
        T result = caseCaseContent(caseContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SUM_CONTENT:
      {
        SumContent sumContent = (SumContent)theEObject;
        T result = caseSumContent(sumContent);
        if (result == null) result = caseFunction(sumContent);
        if (result == null) result = caseTermSimple(sumContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SUM_HEADER:
      {
        SumHeader sumHeader = (SumHeader)theEObject;
        T result = caseSumHeader(sumHeader);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VALUE_CONTENT:
      {
        ValueContent valueContent = (ValueContent)theEObject;
        T result = caseValueContent(valueContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TABLE_CONTENT:
      {
        TableContent tableContent = (TableContent)theEObject;
        T result = caseTableContent(tableContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.WHERE_ITEMS:
      {
        WhereItems whereItems = (WhereItems)theEObject;
        T result = caseWhereItems(whereItems);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ASSIGNMENT:
      {
        Assignment assignment = (Assignment)theEObject;
        T result = caseAssignment(assignment);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TERM_SIMPLE:
      {
        TermSimple termSimple = (TermSimple)theEObject;
        T result = caseTermSimple(termSimple);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LOWER_AND_OR_UPPER:
      {
        LowerAndOrUpper lowerAndOrUpper = (LowerAndOrUpper)theEObject;
        T result = caseLowerAndOrUpper(lowerAndOrUpper);
        if (result == null) result = caseDVarIntegerNonStd(lowerAndOrUpper);
        if (result == null) result = caseDVarInteger(lowerAndOrUpper);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.UPPER_LOWER:
      {
        upperLower upperLower = (upperLower)theEObject;
        T result = caseupperLower(upperLower);
        if (result == null) result = caseLowerAndOrUpper(upperLower);
        if (result == null) result = caseDVarIntegerNonStd(upperLower);
        if (result == null) result = caseDVarInteger(upperLower);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LOWER_UPPER:
      {
        lowerUpper lowerUpper = (lowerUpper)theEObject;
        T result = caselowerUpper(lowerUpper);
        if (result == null) result = caseLowerAndOrUpper(lowerUpper);
        if (result == null) result = caseDVarIntegerNonStd(lowerUpper);
        if (result == null) result = caseDVarInteger(lowerUpper);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.UPPER:
      {
        Upper upper = (Upper)theEObject;
        T result = caseUpper(upper);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LOWER:
      {
        Lower lower = (Lower)theEObject;
        T result = caseLower(lower);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GOAL:
      {
        Goal goal = (Goal)theEObject;
        T result = caseGoal(goal);
        if (result == null) result = casePattern(goal);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GOAL_CASE:
      {
        GoalCase goalCase = (GoalCase)theEObject;
        T result = caseGoalCase(goalCase);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GOAL_CASE_CONTENT:
      {
        GoalCaseContent goalCaseContent = (GoalCaseContent)theEObject;
        T result = caseGoalCaseContent(goalCaseContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT:
      {
        GoalNoCaseContent goalNoCaseContent = (GoalNoCaseContent)theEObject;
        T result = caseGoalNoCaseContent(goalNoCaseContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SUB_CONTENT:
      {
        SubContent subContent = (SubContent)theEObject;
        T result = caseSubContent(subContent);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LHS_GT_RHS:
      {
        LhsGtRhs lhsGtRhs = (LhsGtRhs)theEObject;
        T result = caseLhsGtRhs(lhsGtRhs);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LHS_LT_RHS:
      {
        LhsLtRhs lhsLtRhs = (LhsLtRhs)theEObject;
        T result = caseLhsLtRhs(lhsLtRhs);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.PENALTY:
      {
        Penalty penalty = (Penalty)theEObject;
        T result = casePenalty(penalty);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GOAL_SIMPLE:
      {
        GoalSimple goalSimple = (GoalSimple)theEObject;
        T result = caseGoalSimple(goalSimple);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CONSTRAINT:
      {
        Constraint constraint = (Constraint)theEObject;
        T result = caseConstraint(constraint);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.GROUP:
      {
        Group group = (Group)theEObject;
        T result = caseGroup(group);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.MODEL:
      {
        Model model = (Model)theEObject;
        T result = caseModel(model);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.INITIAL:
      {
        Initial initial = (Initial)theEObject;
        T result = caseInitial(initial);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SEQUENCE:
      {
        Sequence sequence = (Sequence)theEObject;
        T result = caseSequence(sequence);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CONDITION:
      {
        Condition condition = (Condition)theEObject;
        T result = caseCondition(condition);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LOGICAL_EXPRESSION:
      {
        LogicalExpression logicalExpression = (LogicalExpression)theEObject;
        T result = caseLogicalExpression(logicalExpression);
        if (result == null) result = caseConditionalTerm(logicalExpression);
        if (result == null) result = caseConditionalUnary(logicalExpression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CONDITIONAL_UNARY:
      {
        ConditionalUnary conditionalUnary = (ConditionalUnary)theEObject;
        T result = caseConditionalUnary(conditionalUnary);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.CONDITIONAL_TERM:
      {
        ConditionalTerm conditionalTerm = (ConditionalTerm)theEObject;
        T result = caseConditionalTerm(conditionalTerm);
        if (result == null) result = caseConditionalUnary(conditionalTerm);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.EXPRESSION:
      {
        Expression expression = (Expression)theEObject;
        T result = caseExpression(expression);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ADD:
      {
        Add add = (Add)theEObject;
        T result = caseAdd(add);
        if (result == null) result = caseExpression(add);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.MULTIPLY:
      {
        Multiply multiply = (Multiply)theEObject;
        T result = caseMultiply(multiply);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.UNARY:
      {
        Unary unary = (Unary)theEObject;
        T result = caseUnary(unary);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TERM:
      {
        Term term = (Term)theEObject;
        T result = caseTerm(term);
        if (result == null) result = caseUnary(term);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.FUNCTION:
      {
        Function function = (Function)theEObject;
        T result = caseFunction(function);
        if (result == null) result = caseTermSimple(function);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.EXTERNAL_FUNCTION1:
      {
        ExternalFunction1 externalFunction1 = (ExternalFunction1)theEObject;
        T result = caseExternalFunction1(externalFunction1);
        if (result == null) result = caseFunction(externalFunction1);
        if (result == null) result = caseTermSimple(externalFunction1);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.EXTERNAL_FUNCTION2:
      {
        ExternalFunction2 externalFunction2 = (ExternalFunction2)theEObject;
        T result = caseExternalFunction2(externalFunction2);
        if (result == null) result = caseFunction(externalFunction2);
        if (result == null) result = caseTermSimple(externalFunction2);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TRUNK_TIME_ARRAY:
      {
        TrunkTimeArray trunkTimeArray = (TrunkTimeArray)theEObject;
        T result = caseTrunkTimeArray(trunkTimeArray);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TRUNK_TIME_ARRAY_INDEX:
      {
        TrunkTimeArrayIndex trunkTimeArrayIndex = (TrunkTimeArrayIndex)theEObject;
        T result = caseTrunkTimeArrayIndex(trunkTimeArrayIndex);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.MAX_FUNCTION:
      {
        MaxFunction maxFunction = (MaxFunction)theEObject;
        T result = caseMaxFunction(maxFunction);
        if (result == null) result = caseFunction(maxFunction);
        if (result == null) result = caseTermSimple(maxFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.MIN_FUNCTION:
      {
        MinFunction minFunction = (MinFunction)theEObject;
        T result = caseMinFunction(minFunction);
        if (result == null) result = caseFunction(minFunction);
        if (result == null) result = caseTermSimple(minFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.MOD_FUNCTION:
      {
        ModFunction modFunction = (ModFunction)theEObject;
        T result = caseModFunction(modFunction);
        if (result == null) result = caseFunction(modFunction);
        if (result == null) result = caseTermSimple(modFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.INT_FUNCTION:
      {
        IntFunction intFunction = (IntFunction)theEObject;
        T result = caseIntFunction(intFunction);
        if (result == null) result = caseFunction(intFunction);
        if (result == null) result = caseTermSimple(intFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ABS_FUNCTION:
      {
        AbsFunction absFunction = (AbsFunction)theEObject;
        T result = caseAbsFunction(absFunction);
        if (result == null) result = caseFunction(absFunction);
        if (result == null) result = caseTermSimple(absFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ROUND_FUNCTION:
      {
        RoundFunction roundFunction = (RoundFunction)theEObject;
        T result = caseRoundFunction(roundFunction);
        if (result == null) result = caseFunction(roundFunction);
        if (result == null) result = caseTermSimple(roundFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.POW_FUNCTION:
      {
        PowFunction powFunction = (PowFunction)theEObject;
        T result = casePowFunction(powFunction);
        if (result == null) result = caseFunction(powFunction);
        if (result == null) result = caseTermSimple(powFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.LOG_FUNCTION:
      {
        LogFunction logFunction = (LogFunction)theEObject;
        T result = caseLogFunction(logFunction);
        if (result == null) result = caseFunction(logFunction);
        if (result == null) result = caseTermSimple(logFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.SIN_FUNCTION:
      {
        SinFunction sinFunction = (SinFunction)theEObject;
        T result = caseSinFunction(sinFunction);
        if (result == null) result = caseFunction(sinFunction);
        if (result == null) result = caseTermSimple(sinFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.COS_FUNCTION:
      {
        CosFunction cosFunction = (CosFunction)theEObject;
        T result = caseCosFunction(cosFunction);
        if (result == null) result = caseFunction(cosFunction);
        if (result == null) result = caseTermSimple(cosFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.TAN_FUNCTION:
      {
        TanFunction tanFunction = (TanFunction)theEObject;
        T result = caseTanFunction(tanFunction);
        if (result == null) result = caseFunction(tanFunction);
        if (result == null) result = caseTermSimple(tanFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.COT_FUNCTION:
      {
        CotFunction cotFunction = (CotFunction)theEObject;
        T result = caseCotFunction(cotFunction);
        if (result == null) result = caseFunction(cotFunction);
        if (result == null) result = caseTermSimple(cotFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ASIN_FUNCTION:
      {
        AsinFunction asinFunction = (AsinFunction)theEObject;
        T result = caseAsinFunction(asinFunction);
        if (result == null) result = caseFunction(asinFunction);
        if (result == null) result = caseTermSimple(asinFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ACOS_FUNCTION:
      {
        AcosFunction acosFunction = (AcosFunction)theEObject;
        T result = caseAcosFunction(acosFunction);
        if (result == null) result = caseFunction(acosFunction);
        if (result == null) result = caseTermSimple(acosFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ATAN_FUNCTION:
      {
        AtanFunction atanFunction = (AtanFunction)theEObject;
        T result = caseAtanFunction(atanFunction);
        if (result == null) result = caseFunction(atanFunction);
        if (result == null) result = caseTermSimple(atanFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.ACOT_FUNCTION:
      {
        AcotFunction acotFunction = (AcotFunction)theEObject;
        T result = caseAcotFunction(acotFunction);
        if (result == null) result = caseFunction(acotFunction);
        if (result == null) result = caseTermSimple(acotFunction);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VAR_MODEL:
      {
        VarModel varModel = (VarModel)theEObject;
        T result = caseVarModel(varModel);
        if (result == null) result = caseFunction(varModel);
        if (result == null) result = caseTermSimple(varModel);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VAR_MODEL_STEP:
      {
        VarModelStep varModelStep = (VarModelStep)theEObject;
        T result = caseVarModelStep(varModelStep);
        if (result == null) result = caseFunction(varModelStep);
        if (result == null) result = caseTermSimple(varModelStep);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VAR_MODEL_INDEX:
      {
        VarModelIndex varModelIndex = (VarModelIndex)theEObject;
        T result = caseVarModelIndex(varModelIndex);
        if (result == null) result = caseFunction(varModelIndex);
        if (result == null) result = caseTermSimple(varModelIndex);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.VAR_MODEL_INDEX_STEP:
      {
        VarModelIndexStep varModelIndexStep = (VarModelIndexStep)theEObject;
        T result = caseVarModelIndexStep(varModelIndexStep);
        if (result == null) result = caseFunction(varModelIndexStep);
        if (result == null) result = caseTermSimple(varModelIndexStep);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.IDENT:
      {
        Ident ident = (Ident)theEObject;
        T result = caseIdent(ident);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      case WreslEditorPackage.INCLUDE_FILE:
      {
        IncludeFile includeFile = (IncludeFile)theEObject;
        T result = caseIncludeFile(includeFile);
        if (result == null) result = casePattern(includeFile);
        if (result == null) result = defaultCase(theEObject);
        return result;
      }
      default: return defaultCase(theEObject);
    }
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Wresl Evaluator</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Wresl Evaluator</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseWreslEvaluator(WreslEvaluator object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Pattern</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Pattern</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePattern(Pattern object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Declaration</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Declaration</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDeclaration(Declaration object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Variable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Variable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVariable(Variable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>State Variable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>State Variable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseStateVariable(StateVariable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Decision Variable</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Decision Variable</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDecisionVariable(DecisionVariable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>If Inc Items</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>If Inc Items</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIfIncItems(IfIncItems object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>If Term</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>If Term</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIfTerm(IfTerm object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Else If Term</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Else If Term</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseElseIfTerm(ElseIfTerm object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Else Term</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Else Term</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseElseTerm(ElseTerm object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Time Array Size</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Time Array Size</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTimeArraySize(TimeArraySize object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Objective</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Objective</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseObjective(Objective object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Weight Item</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Weight Item</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseWeightItem(WeightItem object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External Def</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternalDef(ExternalDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Svar Def</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Svar Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSvarDef(SvarDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Dvar Def</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Dvar Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDvarDef(DvarDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Const Def</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Const Def</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConstDef(ConstDef object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Alias</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Alias</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAlias(Alias object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternal(External object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVar(DVar object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar Non Std</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar Non Std</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVarNonStd(DVarNonStd object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar Std</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar Std</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVarStd(DVarStd object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar Integer</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar Integer</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVarInteger(DVarInteger object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar Integer Std</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar Integer Std</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVarIntegerStd(DVarIntegerStd object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>DVar Integer Non Std</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>DVar Integer Non Std</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseDVarIntegerNonStd(DVarIntegerNonStd object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVar(SVar object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar DSS</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar DSS</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVarDSS(SVarDSS object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVarExpression(SVarExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar Sum</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar Sum</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVarSum(SVarSum object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar Table</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar Table</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVarTable(SVarTable object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>SVar Case</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>SVar Case</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSVarCase(SVarCase object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Case Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Case Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCaseContent(CaseContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sum Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sum Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSumContent(SumContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sum Header</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sum Header</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSumHeader(SumHeader object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Value Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Value Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseValueContent(ValueContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Table Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Table Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTableContent(TableContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Where Items</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Where Items</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseWhereItems(WhereItems object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Assignment</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Assignment</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAssignment(Assignment object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Term Simple</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Term Simple</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTermSimple(TermSimple object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Lower And Or Upper</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Lower And Or Upper</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLowerAndOrUpper(LowerAndOrUpper object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>upper Lower</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>upper Lower</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseupperLower(upperLower object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>lower Upper</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>lower Upper</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caselowerUpper(lowerUpper object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Upper</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Upper</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUpper(Upper object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Lower</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Lower</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLower(Lower object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Goal</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Goal</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGoal(Goal object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Goal Case</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Goal Case</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGoalCase(GoalCase object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Goal Case Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Goal Case Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGoalCaseContent(GoalCaseContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Goal No Case Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Goal No Case Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGoalNoCaseContent(GoalNoCaseContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sub Content</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sub Content</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSubContent(SubContent object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Lhs Gt Rhs</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Lhs Gt Rhs</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLhsGtRhs(LhsGtRhs object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Lhs Lt Rhs</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Lhs Lt Rhs</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLhsLtRhs(LhsLtRhs object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Penalty</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Penalty</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePenalty(Penalty object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Goal Simple</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Goal Simple</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGoalSimple(GoalSimple object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Constraint</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Constraint</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConstraint(Constraint object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Group</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Group</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseGroup(Group object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModel(Model object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Initial</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Initial</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseInitial(Initial object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sequence</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sequence</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSequence(Sequence object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Condition</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Condition</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCondition(Condition object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Logical Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Logical Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLogicalExpression(LogicalExpression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Conditional Unary</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Conditional Unary</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConditionalUnary(ConditionalUnary object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Conditional Term</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Conditional Term</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseConditionalTerm(ConditionalTerm object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Expression</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Expression</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExpression(Expression object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Add</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Add</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAdd(Add object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Multiply</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Multiply</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMultiply(Multiply object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Unary</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Unary</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseUnary(Unary object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Term</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Term</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTerm(Term object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseFunction(Function object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External Function1</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External Function1</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternalFunction1(ExternalFunction1 object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>External Function2</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>External Function2</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseExternalFunction2(ExternalFunction2 object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Trunk Time Array</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Trunk Time Array</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTrunkTimeArray(TrunkTimeArray object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Trunk Time Array Index</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Trunk Time Array Index</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTrunkTimeArrayIndex(TrunkTimeArrayIndex object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Max Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Max Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMaxFunction(MaxFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Min Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Min Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseMinFunction(MinFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Mod Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Mod Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseModFunction(ModFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Int Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Int Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIntFunction(IntFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Abs Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Abs Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAbsFunction(AbsFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Round Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Round Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseRoundFunction(RoundFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Pow Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Pow Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T casePowFunction(PowFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Log Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Log Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseLogFunction(LogFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Sin Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Sin Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseSinFunction(SinFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Cos Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Cos Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCosFunction(CosFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Tan Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Tan Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseTanFunction(TanFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Cot Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Cot Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseCotFunction(CotFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Asin Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Asin Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAsinFunction(AsinFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Acos Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Acos Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAcosFunction(AcosFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Atan Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Atan Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAtanFunction(AtanFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Acot Function</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Acot Function</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseAcotFunction(AcotFunction object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Var Model</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Var Model</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVarModel(VarModel object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Var Model Step</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Var Model Step</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVarModelStep(VarModelStep object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Var Model Index</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Var Model Index</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVarModelIndex(VarModelIndex object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Var Model Index Step</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Var Model Index Step</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseVarModelIndexStep(VarModelIndexStep object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Ident</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Ident</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIdent(Ident object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>Include File</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>Include File</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject) doSwitch(EObject)
   * @generated
   */
  public T caseIncludeFile(IncludeFile object)
  {
    return null;
  }

  /**
   * Returns the result of interpreting the object as an instance of '<em>EObject</em>'.
   * <!-- begin-user-doc -->
   * This implementation returns null;
   * returning a non-null result will terminate the switch, but this is the last case anyway.
   * <!-- end-user-doc -->
   * @param object the target of the switch.
   * @return the result of interpreting the object as an instance of '<em>EObject</em>'.
   * @see #doSwitch(org.eclipse.emf.ecore.EObject)
   * @generated
   */
  @Override
  public T defaultCase(EObject object)
  {
    return null;
  }

} //WreslEditorSwitch
