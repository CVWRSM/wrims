/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WreslEditorFactoryImpl extends EFactoryImpl implements WreslEditorFactory
{
  /**
   * Creates the default factory implementation.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public static WreslEditorFactory init()
  {
    try
    {
      WreslEditorFactory theWreslEditorFactory = (WreslEditorFactory)EPackage.Registry.INSTANCE.getEFactory(WreslEditorPackage.eNS_URI);
      if (theWreslEditorFactory != null)
      {
        return theWreslEditorFactory;
      }
    }
    catch (Exception exception)
    {
      EcorePlugin.INSTANCE.log(exception);
    }
    return new WreslEditorFactoryImpl();
  }

  /**
   * Creates an instance of the factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEditorFactoryImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public EObject create(EClass eClass)
  {
    switch (eClass.getClassifierID())
    {
      case WreslEditorPackage.WRESL_EVALUATOR: return createWreslEvaluator();
      case WreslEditorPackage.PATTERN: return createPattern();
      case WreslEditorPackage.DECLARATION: return createDeclaration();
      case WreslEditorPackage.VARIABLE: return createVariable();
      case WreslEditorPackage.STATE_VARIABLE: return createStateVariable();
      case WreslEditorPackage.DECISION_VARIABLE: return createDecisionVariable();
      case WreslEditorPackage.IF_INC_ITEMS: return createIfIncItems();
      case WreslEditorPackage.IF_TERM: return createIfTerm();
      case WreslEditorPackage.ELSE_IF_TERM: return createElseIfTerm();
      case WreslEditorPackage.ELSE_TERM: return createElseTerm();
      case WreslEditorPackage.TIME_ARRAY_SIZE: return createTimeArraySize();
      case WreslEditorPackage.OBJECTIVE: return createObjective();
      case WreslEditorPackage.WEIGHT_ITEM: return createWeightItem();
      case WreslEditorPackage.EXTERNAL_DEF: return createExternalDef();
      case WreslEditorPackage.SVAR_DEF: return createSvarDef();
      case WreslEditorPackage.DVAR_DEF: return createDvarDef();
      case WreslEditorPackage.CONST_DEF: return createConstDef();
      case WreslEditorPackage.ALIAS: return createAlias();
      case WreslEditorPackage.EXTERNAL: return createExternal();
      case WreslEditorPackage.DVAR: return createDVar();
      case WreslEditorPackage.DVAR_NON_STD: return createDVarNonStd();
      case WreslEditorPackage.DVAR_STD: return createDVarStd();
      case WreslEditorPackage.DVAR_INTEGER: return createDVarInteger();
      case WreslEditorPackage.DVAR_INTEGER_STD: return createDVarIntegerStd();
      case WreslEditorPackage.DVAR_INTEGER_NON_STD: return createDVarIntegerNonStd();
      case WreslEditorPackage.SVAR: return createSVar();
      case WreslEditorPackage.SVAR_DSS: return createSVarDSS();
      case WreslEditorPackage.SVAR_EXPRESSION: return createSVarExpression();
      case WreslEditorPackage.SVAR_SUM: return createSVarSum();
      case WreslEditorPackage.SVAR_TABLE: return createSVarTable();
      case WreslEditorPackage.SVAR_CASE: return createSVarCase();
      case WreslEditorPackage.CASE_CONTENT: return createCaseContent();
      case WreslEditorPackage.SUM_CONTENT: return createSumContent();
      case WreslEditorPackage.SUM_HEADER: return createSumHeader();
      case WreslEditorPackage.VALUE_CONTENT: return createValueContent();
      case WreslEditorPackage.TABLE_CONTENT: return createTableContent();
      case WreslEditorPackage.WHERE_ITEMS: return createWhereItems();
      case WreslEditorPackage.ASSIGNMENT: return createAssignment();
      case WreslEditorPackage.TERM_SIMPLE: return createTermSimple();
      case WreslEditorPackage.LOWER_AND_OR_UPPER: return createLowerAndOrUpper();
      case WreslEditorPackage.UPPER_LOWER: return createupperLower();
      case WreslEditorPackage.LOWER_UPPER: return createlowerUpper();
      case WreslEditorPackage.UPPER: return createUpper();
      case WreslEditorPackage.LOWER: return createLower();
      case WreslEditorPackage.GOAL: return createGoal();
      case WreslEditorPackage.GOAL_CASE: return createGoalCase();
      case WreslEditorPackage.GOAL_CASE_CONTENT: return createGoalCaseContent();
      case WreslEditorPackage.GOAL_NO_CASE_CONTENT: return createGoalNoCaseContent();
      case WreslEditorPackage.SUB_CONTENT: return createSubContent();
      case WreslEditorPackage.LHS_GT_RHS: return createLhsGtRhs();
      case WreslEditorPackage.LHS_LT_RHS: return createLhsLtRhs();
      case WreslEditorPackage.PENALTY: return createPenalty();
      case WreslEditorPackage.GOAL_SIMPLE: return createGoalSimple();
      case WreslEditorPackage.CONSTRAINT: return createConstraint();
      case WreslEditorPackage.GROUP: return createGroup();
      case WreslEditorPackage.MODEL: return createModel();
      case WreslEditorPackage.INITIAL: return createInitial();
      case WreslEditorPackage.SEQUENCE: return createSequence();
      case WreslEditorPackage.CONDITION: return createCondition();
      case WreslEditorPackage.LOGICAL_EXPRESSION: return createLogicalExpression();
      case WreslEditorPackage.CONDITIONAL_UNARY: return createConditionalUnary();
      case WreslEditorPackage.CONDITIONAL_TERM: return createConditionalTerm();
      case WreslEditorPackage.EXPRESSION: return createExpression();
      case WreslEditorPackage.ADD: return createAdd();
      case WreslEditorPackage.MULTIPLY: return createMultiply();
      case WreslEditorPackage.UNARY: return createUnary();
      case WreslEditorPackage.TERM: return createTerm();
      case WreslEditorPackage.FUNCTION: return createFunction();
      case WreslEditorPackage.EXTERNAL_FUNCTION1: return createExternalFunction1();
      case WreslEditorPackage.EXTERNAL_FUNCTION2: return createExternalFunction2();
      case WreslEditorPackage.TRUNK_TIME_ARRAY: return createTrunkTimeArray();
      case WreslEditorPackage.TRUNK_TIME_ARRAY_INDEX: return createTrunkTimeArrayIndex();
      case WreslEditorPackage.MAX_FUNCTION: return createMaxFunction();
      case WreslEditorPackage.MIN_FUNCTION: return createMinFunction();
      case WreslEditorPackage.MOD_FUNCTION: return createModFunction();
      case WreslEditorPackage.INT_FUNCTION: return createIntFunction();
      case WreslEditorPackage.ABS_FUNCTION: return createAbsFunction();
      case WreslEditorPackage.ROUND_FUNCTION: return createRoundFunction();
      case WreslEditorPackage.POW_FUNCTION: return createPowFunction();
      case WreslEditorPackage.LOG_FUNCTION: return createLogFunction();
      case WreslEditorPackage.SIN_FUNCTION: return createSinFunction();
      case WreslEditorPackage.COS_FUNCTION: return createCosFunction();
      case WreslEditorPackage.TAN_FUNCTION: return createTanFunction();
      case WreslEditorPackage.COT_FUNCTION: return createCotFunction();
      case WreslEditorPackage.ASIN_FUNCTION: return createAsinFunction();
      case WreslEditorPackage.ACOS_FUNCTION: return createAcosFunction();
      case WreslEditorPackage.ATAN_FUNCTION: return createAtanFunction();
      case WreslEditorPackage.ACOT_FUNCTION: return createAcotFunction();
      case WreslEditorPackage.VAR_MODEL: return createVarModel();
      case WreslEditorPackage.VAR_MODEL_STEP: return createVarModelStep();
      case WreslEditorPackage.VAR_MODEL_INDEX: return createVarModelIndex();
      case WreslEditorPackage.VAR_MODEL_INDEX_STEP: return createVarModelIndexStep();
      case WreslEditorPackage.IDENT: return createIdent();
      case WreslEditorPackage.INCLUDE_FILE: return createIncludeFile();
      default:
        throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
    }
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEvaluator createWreslEvaluator()
  {
    WreslEvaluatorImpl wreslEvaluator = new WreslEvaluatorImpl();
    return wreslEvaluator;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Pattern createPattern()
  {
    PatternImpl pattern = new PatternImpl();
    return pattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Declaration createDeclaration()
  {
    DeclarationImpl declaration = new DeclarationImpl();
    return declaration;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Variable createVariable()
  {
    VariableImpl variable = new VariableImpl();
    return variable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public StateVariable createStateVariable()
  {
    StateVariableImpl stateVariable = new StateVariableImpl();
    return stateVariable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DecisionVariable createDecisionVariable()
  {
    DecisionVariableImpl decisionVariable = new DecisionVariableImpl();
    return decisionVariable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IfIncItems createIfIncItems()
  {
    IfIncItemsImpl ifIncItems = new IfIncItemsImpl();
    return ifIncItems;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IfTerm createIfTerm()
  {
    IfTermImpl ifTerm = new IfTermImpl();
    return ifTerm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElseIfTerm createElseIfTerm()
  {
    ElseIfTermImpl elseIfTerm = new ElseIfTermImpl();
    return elseIfTerm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElseTerm createElseTerm()
  {
    ElseTermImpl elseTerm = new ElseTermImpl();
    return elseTerm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TimeArraySize createTimeArraySize()
  {
    TimeArraySizeImpl timeArraySize = new TimeArraySizeImpl();
    return timeArraySize;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Objective createObjective()
  {
    ObjectiveImpl objective = new ObjectiveImpl();
    return objective;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WeightItem createWeightItem()
  {
    WeightItemImpl weightItem = new WeightItemImpl();
    return weightItem;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExternalDef createExternalDef()
  {
    ExternalDefImpl externalDef = new ExternalDefImpl();
    return externalDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SvarDef createSvarDef()
  {
    SvarDefImpl svarDef = new SvarDefImpl();
    return svarDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DvarDef createDvarDef()
  {
    DvarDefImpl dvarDef = new DvarDefImpl();
    return dvarDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConstDef createConstDef()
  {
    ConstDefImpl constDef = new ConstDefImpl();
    return constDef;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Alias createAlias()
  {
    AliasImpl alias = new AliasImpl();
    return alias;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public External createExternal()
  {
    ExternalImpl external = new ExternalImpl();
    return external;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVar createDVar()
  {
    DVarImpl dVar = new DVarImpl();
    return dVar;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVarNonStd createDVarNonStd()
  {
    DVarNonStdImpl dVarNonStd = new DVarNonStdImpl();
    return dVarNonStd;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVarStd createDVarStd()
  {
    DVarStdImpl dVarStd = new DVarStdImpl();
    return dVarStd;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVarInteger createDVarInteger()
  {
    DVarIntegerImpl dVarInteger = new DVarIntegerImpl();
    return dVarInteger;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVarIntegerStd createDVarIntegerStd()
  {
    DVarIntegerStdImpl dVarIntegerStd = new DVarIntegerStdImpl();
    return dVarIntegerStd;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DVarIntegerNonStd createDVarIntegerNonStd()
  {
    DVarIntegerNonStdImpl dVarIntegerNonStd = new DVarIntegerNonStdImpl();
    return dVarIntegerNonStd;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVar createSVar()
  {
    SVarImpl sVar = new SVarImpl();
    return sVar;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVarDSS createSVarDSS()
  {
    SVarDSSImpl sVarDSS = new SVarDSSImpl();
    return sVarDSS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVarExpression createSVarExpression()
  {
    SVarExpressionImpl sVarExpression = new SVarExpressionImpl();
    return sVarExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVarSum createSVarSum()
  {
    SVarSumImpl sVarSum = new SVarSumImpl();
    return sVarSum;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVarTable createSVarTable()
  {
    SVarTableImpl sVarTable = new SVarTableImpl();
    return sVarTable;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SVarCase createSVarCase()
  {
    SVarCaseImpl sVarCase = new SVarCaseImpl();
    return sVarCase;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CaseContent createCaseContent()
  {
    CaseContentImpl caseContent = new CaseContentImpl();
    return caseContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SumContent createSumContent()
  {
    SumContentImpl sumContent = new SumContentImpl();
    return sumContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SumHeader createSumHeader()
  {
    SumHeaderImpl sumHeader = new SumHeaderImpl();
    return sumHeader;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ValueContent createValueContent()
  {
    ValueContentImpl valueContent = new ValueContentImpl();
    return valueContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TableContent createTableContent()
  {
    TableContentImpl tableContent = new TableContentImpl();
    return tableContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WhereItems createWhereItems()
  {
    WhereItemsImpl whereItems = new WhereItemsImpl();
    return whereItems;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Assignment createAssignment()
  {
    AssignmentImpl assignment = new AssignmentImpl();
    return assignment;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TermSimple createTermSimple()
  {
    TermSimpleImpl termSimple = new TermSimpleImpl();
    return termSimple;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LowerAndOrUpper createLowerAndOrUpper()
  {
    LowerAndOrUpperImpl lowerAndOrUpper = new LowerAndOrUpperImpl();
    return lowerAndOrUpper;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public upperLower createupperLower()
  {
    upperLowerImpl upperLower = new upperLowerImpl();
    return upperLower;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public lowerUpper createlowerUpper()
  {
    lowerUpperImpl lowerUpper = new lowerUpperImpl();
    return lowerUpper;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Upper createUpper()
  {
    UpperImpl upper = new UpperImpl();
    return upper;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Lower createLower()
  {
    LowerImpl lower = new LowerImpl();
    return lower;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Goal createGoal()
  {
    GoalImpl goal = new GoalImpl();
    return goal;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GoalCase createGoalCase()
  {
    GoalCaseImpl goalCase = new GoalCaseImpl();
    return goalCase;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GoalCaseContent createGoalCaseContent()
  {
    GoalCaseContentImpl goalCaseContent = new GoalCaseContentImpl();
    return goalCaseContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GoalNoCaseContent createGoalNoCaseContent()
  {
    GoalNoCaseContentImpl goalNoCaseContent = new GoalNoCaseContentImpl();
    return goalNoCaseContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SubContent createSubContent()
  {
    SubContentImpl subContent = new SubContentImpl();
    return subContent;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LhsGtRhs createLhsGtRhs()
  {
    LhsGtRhsImpl lhsGtRhs = new LhsGtRhsImpl();
    return lhsGtRhs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LhsLtRhs createLhsLtRhs()
  {
    LhsLtRhsImpl lhsLtRhs = new LhsLtRhsImpl();
    return lhsLtRhs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Penalty createPenalty()
  {
    PenaltyImpl penalty = new PenaltyImpl();
    return penalty;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public GoalSimple createGoalSimple()
  {
    GoalSimpleImpl goalSimple = new GoalSimpleImpl();
    return goalSimple;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Constraint createConstraint()
  {
    ConstraintImpl constraint = new ConstraintImpl();
    return constraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Group createGroup()
  {
    GroupImpl group = new GroupImpl();
    return group;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Model createModel()
  {
    ModelImpl model = new ModelImpl();
    return model;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Initial createInitial()
  {
    InitialImpl initial = new InitialImpl();
    return initial;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Sequence createSequence()
  {
    SequenceImpl sequence = new SequenceImpl();
    return sequence;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Condition createCondition()
  {
    ConditionImpl condition = new ConditionImpl();
    return condition;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LogicalExpression createLogicalExpression()
  {
    LogicalExpressionImpl logicalExpression = new LogicalExpressionImpl();
    return logicalExpression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConditionalUnary createConditionalUnary()
  {
    ConditionalUnaryImpl conditionalUnary = new ConditionalUnaryImpl();
    return conditionalUnary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConditionalTerm createConditionalTerm()
  {
    ConditionalTermImpl conditionalTerm = new ConditionalTermImpl();
    return conditionalTerm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Expression createExpression()
  {
    ExpressionImpl expression = new ExpressionImpl();
    return expression;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Add createAdd()
  {
    AddImpl add = new AddImpl();
    return add;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Multiply createMultiply()
  {
    MultiplyImpl multiply = new MultiplyImpl();
    return multiply;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Unary createUnary()
  {
    UnaryImpl unary = new UnaryImpl();
    return unary;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Term createTerm()
  {
    TermImpl term = new TermImpl();
    return term;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Function createFunction()
  {
    FunctionImpl function = new FunctionImpl();
    return function;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExternalFunction1 createExternalFunction1()
  {
    ExternalFunction1Impl externalFunction1 = new ExternalFunction1Impl();
    return externalFunction1;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ExternalFunction2 createExternalFunction2()
  {
    ExternalFunction2Impl externalFunction2 = new ExternalFunction2Impl();
    return externalFunction2;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TrunkTimeArray createTrunkTimeArray()
  {
    TrunkTimeArrayImpl trunkTimeArray = new TrunkTimeArrayImpl();
    return trunkTimeArray;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TrunkTimeArrayIndex createTrunkTimeArrayIndex()
  {
    TrunkTimeArrayIndexImpl trunkTimeArrayIndex = new TrunkTimeArrayIndexImpl();
    return trunkTimeArrayIndex;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MaxFunction createMaxFunction()
  {
    MaxFunctionImpl maxFunction = new MaxFunctionImpl();
    return maxFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public MinFunction createMinFunction()
  {
    MinFunctionImpl minFunction = new MinFunctionImpl();
    return minFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ModFunction createModFunction()
  {
    ModFunctionImpl modFunction = new ModFunctionImpl();
    return modFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IntFunction createIntFunction()
  {
    IntFunctionImpl intFunction = new IntFunctionImpl();
    return intFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AbsFunction createAbsFunction()
  {
    AbsFunctionImpl absFunction = new AbsFunctionImpl();
    return absFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public RoundFunction createRoundFunction()
  {
    RoundFunctionImpl roundFunction = new RoundFunctionImpl();
    return roundFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public PowFunction createPowFunction()
  {
    PowFunctionImpl powFunction = new PowFunctionImpl();
    return powFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LogFunction createLogFunction()
  {
    LogFunctionImpl logFunction = new LogFunctionImpl();
    return logFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public SinFunction createSinFunction()
  {
    SinFunctionImpl sinFunction = new SinFunctionImpl();
    return sinFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CosFunction createCosFunction()
  {
    CosFunctionImpl cosFunction = new CosFunctionImpl();
    return cosFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public TanFunction createTanFunction()
  {
    TanFunctionImpl tanFunction = new TanFunctionImpl();
    return tanFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public CotFunction createCotFunction()
  {
    CotFunctionImpl cotFunction = new CotFunctionImpl();
    return cotFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AsinFunction createAsinFunction()
  {
    AsinFunctionImpl asinFunction = new AsinFunctionImpl();
    return asinFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AcosFunction createAcosFunction()
  {
    AcosFunctionImpl acosFunction = new AcosFunctionImpl();
    return acosFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AtanFunction createAtanFunction()
  {
    AtanFunctionImpl atanFunction = new AtanFunctionImpl();
    return atanFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public AcotFunction createAcotFunction()
  {
    AcotFunctionImpl acotFunction = new AcotFunctionImpl();
    return acotFunction;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VarModel createVarModel()
  {
    VarModelImpl varModel = new VarModelImpl();
    return varModel;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VarModelStep createVarModelStep()
  {
    VarModelStepImpl varModelStep = new VarModelStepImpl();
    return varModelStep;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VarModelIndex createVarModelIndex()
  {
    VarModelIndexImpl varModelIndex = new VarModelIndexImpl();
    return varModelIndex;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public VarModelIndexStep createVarModelIndexStep()
  {
    VarModelIndexStepImpl varModelIndexStep = new VarModelIndexStepImpl();
    return varModelIndexStep;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Ident createIdent()
  {
    IdentImpl ident = new IdentImpl();
    return ident;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public IncludeFile createIncludeFile()
  {
    IncludeFileImpl includeFile = new IncludeFileImpl();
    return includeFile;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEditorPackage getWreslEditorPackage()
  {
    return (WreslEditorPackage)getEPackage();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @deprecated
   * @generated
   */
  @Deprecated
  public static WreslEditorPackage getPackage()
  {
    return WreslEditorPackage.eINSTANCE;
  }

} //WreslEditorFactoryImpl
