/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.util;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.*;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;

import org.eclipse.emf.common.notify.impl.AdapterFactoryImpl;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * The <b>Adapter Factory</b> for the model.
 * It provides an adapter <code>createXXX</code> method for each class of the model.
 * <!-- end-user-doc -->
 * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage
 * @generated
 */
public class WreslEditorAdapterFactory extends AdapterFactoryImpl
{
  /**
   * The cached model package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected static WreslEditorPackage modelPackage;

  /**
   * Creates an instance of the adapter factory.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEditorAdapterFactory()
  {
    if (modelPackage == null)
    {
      modelPackage = WreslEditorPackage.eINSTANCE;
    }
  }

  /**
   * Returns whether this factory is applicable for the type of the object.
   * <!-- begin-user-doc -->
   * This implementation returns <code>true</code> if the object is either the model's package or is an instance object of the model.
   * <!-- end-user-doc -->
   * @return whether this factory is applicable for the type of the object.
   * @generated
   */
  @Override
  public boolean isFactoryForType(Object object)
  {
    if (object == modelPackage)
    {
      return true;
    }
    if (object instanceof EObject)
    {
      return ((EObject)object).eClass().getEPackage() == modelPackage;
    }
    return false;
  }

  /**
   * The switch that delegates to the <code>createXXX</code> methods.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WreslEditorSwitch<Adapter> modelSwitch =
    new WreslEditorSwitch<Adapter>()
    {
      @Override
      public Adapter caseWreslEvaluator(WreslEvaluator object)
      {
        return createWreslEvaluatorAdapter();
      }
      @Override
      public Adapter casePattern(Pattern object)
      {
        return createPatternAdapter();
      }
      @Override
      public Adapter caseDeclaration(Declaration object)
      {
        return createDeclarationAdapter();
      }
      @Override
      public Adapter caseVariable(Variable object)
      {
        return createVariableAdapter();
      }
      @Override
      public Adapter caseStateVariable(StateVariable object)
      {
        return createStateVariableAdapter();
      }
      @Override
      public Adapter caseDecisionVariable(DecisionVariable object)
      {
        return createDecisionVariableAdapter();
      }
      @Override
      public Adapter caseIfIncItems(IfIncItems object)
      {
        return createIfIncItemsAdapter();
      }
      @Override
      public Adapter caseIfTerm(IfTerm object)
      {
        return createIfTermAdapter();
      }
      @Override
      public Adapter caseElseIfTerm(ElseIfTerm object)
      {
        return createElseIfTermAdapter();
      }
      @Override
      public Adapter caseElseTerm(ElseTerm object)
      {
        return createElseTermAdapter();
      }
      @Override
      public Adapter caseTimeArraySize(TimeArraySize object)
      {
        return createTimeArraySizeAdapter();
      }
      @Override
      public Adapter caseObjective(Objective object)
      {
        return createObjectiveAdapter();
      }
      @Override
      public Adapter caseWeightItem(WeightItem object)
      {
        return createWeightItemAdapter();
      }
      @Override
      public Adapter caseExternalDef(ExternalDef object)
      {
        return createExternalDefAdapter();
      }
      @Override
      public Adapter caseSvarDef(SvarDef object)
      {
        return createSvarDefAdapter();
      }
      @Override
      public Adapter caseDvarDef(DvarDef object)
      {
        return createDvarDefAdapter();
      }
      @Override
      public Adapter caseConstDef(ConstDef object)
      {
        return createConstDefAdapter();
      }
      @Override
      public Adapter caseAlias(Alias object)
      {
        return createAliasAdapter();
      }
      @Override
      public Adapter caseExternal(External object)
      {
        return createExternalAdapter();
      }
      @Override
      public Adapter caseDVar(DVar object)
      {
        return createDVarAdapter();
      }
      @Override
      public Adapter caseDVarNonStd(DVarNonStd object)
      {
        return createDVarNonStdAdapter();
      }
      @Override
      public Adapter caseDVarStd(DVarStd object)
      {
        return createDVarStdAdapter();
      }
      @Override
      public Adapter caseDVarInteger(DVarInteger object)
      {
        return createDVarIntegerAdapter();
      }
      @Override
      public Adapter caseDVarIntegerStd(DVarIntegerStd object)
      {
        return createDVarIntegerStdAdapter();
      }
      @Override
      public Adapter caseDVarIntegerNonStd(DVarIntegerNonStd object)
      {
        return createDVarIntegerNonStdAdapter();
      }
      @Override
      public Adapter caseSVar(SVar object)
      {
        return createSVarAdapter();
      }
      @Override
      public Adapter caseSVarDSS(SVarDSS object)
      {
        return createSVarDSSAdapter();
      }
      @Override
      public Adapter caseSVarExpression(SVarExpression object)
      {
        return createSVarExpressionAdapter();
      }
      @Override
      public Adapter caseSVarSum(SVarSum object)
      {
        return createSVarSumAdapter();
      }
      @Override
      public Adapter caseSVarTable(SVarTable object)
      {
        return createSVarTableAdapter();
      }
      @Override
      public Adapter caseSVarCase(SVarCase object)
      {
        return createSVarCaseAdapter();
      }
      @Override
      public Adapter caseCaseContent(CaseContent object)
      {
        return createCaseContentAdapter();
      }
      @Override
      public Adapter caseSumContent(SumContent object)
      {
        return createSumContentAdapter();
      }
      @Override
      public Adapter caseSumHeader(SumHeader object)
      {
        return createSumHeaderAdapter();
      }
      @Override
      public Adapter caseValueContent(ValueContent object)
      {
        return createValueContentAdapter();
      }
      @Override
      public Adapter caseTableContent(TableContent object)
      {
        return createTableContentAdapter();
      }
      @Override
      public Adapter caseWhereItems(WhereItems object)
      {
        return createWhereItemsAdapter();
      }
      @Override
      public Adapter caseAssignment(Assignment object)
      {
        return createAssignmentAdapter();
      }
      @Override
      public Adapter caseTermSimple(TermSimple object)
      {
        return createTermSimpleAdapter();
      }
      @Override
      public Adapter caseLowerAndOrUpper(LowerAndOrUpper object)
      {
        return createLowerAndOrUpperAdapter();
      }
      @Override
      public Adapter caseupperLower(upperLower object)
      {
        return createupperLowerAdapter();
      }
      @Override
      public Adapter caselowerUpper(lowerUpper object)
      {
        return createlowerUpperAdapter();
      }
      @Override
      public Adapter caseUpper(Upper object)
      {
        return createUpperAdapter();
      }
      @Override
      public Adapter caseLower(Lower object)
      {
        return createLowerAdapter();
      }
      @Override
      public Adapter caseGoal(Goal object)
      {
        return createGoalAdapter();
      }
      @Override
      public Adapter caseGoalCase(GoalCase object)
      {
        return createGoalCaseAdapter();
      }
      @Override
      public Adapter caseGoalCaseContent(GoalCaseContent object)
      {
        return createGoalCaseContentAdapter();
      }
      @Override
      public Adapter caseGoalNoCaseContent(GoalNoCaseContent object)
      {
        return createGoalNoCaseContentAdapter();
      }
      @Override
      public Adapter caseSubContent(SubContent object)
      {
        return createSubContentAdapter();
      }
      @Override
      public Adapter caseLhsGtRhs(LhsGtRhs object)
      {
        return createLhsGtRhsAdapter();
      }
      @Override
      public Adapter caseLhsLtRhs(LhsLtRhs object)
      {
        return createLhsLtRhsAdapter();
      }
      @Override
      public Adapter casePenalty(Penalty object)
      {
        return createPenaltyAdapter();
      }
      @Override
      public Adapter caseGoalSimple(GoalSimple object)
      {
        return createGoalSimpleAdapter();
      }
      @Override
      public Adapter caseConstraint(Constraint object)
      {
        return createConstraintAdapter();
      }
      @Override
      public Adapter caseGroup(Group object)
      {
        return createGroupAdapter();
      }
      @Override
      public Adapter caseModel(Model object)
      {
        return createModelAdapter();
      }
      @Override
      public Adapter caseInitial(Initial object)
      {
        return createInitialAdapter();
      }
      @Override
      public Adapter caseSequence(Sequence object)
      {
        return createSequenceAdapter();
      }
      @Override
      public Adapter caseCondition(Condition object)
      {
        return createConditionAdapter();
      }
      @Override
      public Adapter caseLogicalExpression(LogicalExpression object)
      {
        return createLogicalExpressionAdapter();
      }
      @Override
      public Adapter caseConditionalUnary(ConditionalUnary object)
      {
        return createConditionalUnaryAdapter();
      }
      @Override
      public Adapter caseConditionalTerm(ConditionalTerm object)
      {
        return createConditionalTermAdapter();
      }
      @Override
      public Adapter caseExpression(Expression object)
      {
        return createExpressionAdapter();
      }
      @Override
      public Adapter caseAdd(Add object)
      {
        return createAddAdapter();
      }
      @Override
      public Adapter caseMultiply(Multiply object)
      {
        return createMultiplyAdapter();
      }
      @Override
      public Adapter caseUnary(Unary object)
      {
        return createUnaryAdapter();
      }
      @Override
      public Adapter caseTerm(Term object)
      {
        return createTermAdapter();
      }
      @Override
      public Adapter caseFunction(Function object)
      {
        return createFunctionAdapter();
      }
      @Override
      public Adapter caseExternalFunction1(ExternalFunction1 object)
      {
        return createExternalFunction1Adapter();
      }
      @Override
      public Adapter caseExternalFunction2(ExternalFunction2 object)
      {
        return createExternalFunction2Adapter();
      }
      @Override
      public Adapter caseTrunkTimeArray(TrunkTimeArray object)
      {
        return createTrunkTimeArrayAdapter();
      }
      @Override
      public Adapter caseTrunkTimeArrayIndex(TrunkTimeArrayIndex object)
      {
        return createTrunkTimeArrayIndexAdapter();
      }
      @Override
      public Adapter caseMaxFunction(MaxFunction object)
      {
        return createMaxFunctionAdapter();
      }
      @Override
      public Adapter caseMinFunction(MinFunction object)
      {
        return createMinFunctionAdapter();
      }
      @Override
      public Adapter caseModFunction(ModFunction object)
      {
        return createModFunctionAdapter();
      }
      @Override
      public Adapter caseIntFunction(IntFunction object)
      {
        return createIntFunctionAdapter();
      }
      @Override
      public Adapter caseAbsFunction(AbsFunction object)
      {
        return createAbsFunctionAdapter();
      }
      @Override
      public Adapter caseRoundFunction(RoundFunction object)
      {
        return createRoundFunctionAdapter();
      }
      @Override
      public Adapter casePowFunction(PowFunction object)
      {
        return createPowFunctionAdapter();
      }
      @Override
      public Adapter caseLogFunction(LogFunction object)
      {
        return createLogFunctionAdapter();
      }
      @Override
      public Adapter caseSinFunction(SinFunction object)
      {
        return createSinFunctionAdapter();
      }
      @Override
      public Adapter caseCosFunction(CosFunction object)
      {
        return createCosFunctionAdapter();
      }
      @Override
      public Adapter caseTanFunction(TanFunction object)
      {
        return createTanFunctionAdapter();
      }
      @Override
      public Adapter caseCotFunction(CotFunction object)
      {
        return createCotFunctionAdapter();
      }
      @Override
      public Adapter caseAsinFunction(AsinFunction object)
      {
        return createAsinFunctionAdapter();
      }
      @Override
      public Adapter caseAcosFunction(AcosFunction object)
      {
        return createAcosFunctionAdapter();
      }
      @Override
      public Adapter caseAtanFunction(AtanFunction object)
      {
        return createAtanFunctionAdapter();
      }
      @Override
      public Adapter caseAcotFunction(AcotFunction object)
      {
        return createAcotFunctionAdapter();
      }
      @Override
      public Adapter caseVarModel(VarModel object)
      {
        return createVarModelAdapter();
      }
      @Override
      public Adapter caseVarModelStep(VarModelStep object)
      {
        return createVarModelStepAdapter();
      }
      @Override
      public Adapter caseVarModelIndex(VarModelIndex object)
      {
        return createVarModelIndexAdapter();
      }
      @Override
      public Adapter caseVarModelIndexStep(VarModelIndexStep object)
      {
        return createVarModelIndexStepAdapter();
      }
      @Override
      public Adapter caseIdent(Ident object)
      {
        return createIdentAdapter();
      }
      @Override
      public Adapter caseIncludeFile(IncludeFile object)
      {
        return createIncludeFileAdapter();
      }
      @Override
      public Adapter defaultCase(EObject object)
      {
        return createEObjectAdapter();
      }
    };

  /**
   * Creates an adapter for the <code>target</code>.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @param target the object to adapt.
   * @return the adapter for the <code>target</code>.
   * @generated
   */
  @Override
  public Adapter createAdapter(Notifier target)
  {
    return modelSwitch.doSwitch((EObject)target);
  }


  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEvaluator <em>Wresl Evaluator</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEvaluator
   * @generated
   */
  public Adapter createWreslEvaluatorAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern <em>Pattern</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern
   * @generated
   */
  public Adapter createPatternAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Declaration <em>Declaration</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Declaration
   * @generated
   */
  public Adapter createDeclarationAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Variable <em>Variable</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Variable
   * @generated
   */
  public Adapter createVariableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.StateVariable <em>State Variable</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.StateVariable
   * @generated
   */
  public Adapter createStateVariableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable <em>Decision Variable</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable
   * @generated
   */
  public Adapter createDecisionVariableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfIncItems <em>If Inc Items</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfIncItems
   * @generated
   */
  public Adapter createIfIncItemsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm <em>If Term</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm
   * @generated
   */
  public Adapter createIfTermAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseIfTerm <em>Else If Term</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseIfTerm
   * @generated
   */
  public Adapter createElseIfTermAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseTerm <em>Else Term</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseTerm
   * @generated
   */
  public Adapter createElseTermAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TimeArraySize <em>Time Array Size</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TimeArraySize
   * @generated
   */
  public Adapter createTimeArraySizeAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Objective <em>Objective</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Objective
   * @generated
   */
  public Adapter createObjectiveAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.WeightItem <em>Weight Item</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WeightItem
   * @generated
   */
  public Adapter createWeightItemAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalDef <em>External Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalDef
   * @generated
   */
  public Adapter createExternalDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SvarDef <em>Svar Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SvarDef
   * @generated
   */
  public Adapter createSvarDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DvarDef <em>Dvar Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DvarDef
   * @generated
   */
  public Adapter createDvarDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConstDef <em>Const Def</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConstDef
   * @generated
   */
  public Adapter createConstDefAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Alias <em>Alias</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Alias
   * @generated
   */
  public Adapter createAliasAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.External <em>External</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.External
   * @generated
   */
  public Adapter createExternalAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVar <em>DVar</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVar
   * @generated
   */
  public Adapter createDVarAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarNonStd <em>DVar Non Std</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarNonStd
   * @generated
   */
  public Adapter createDVarNonStdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarStd <em>DVar Std</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarStd
   * @generated
   */
  public Adapter createDVarStdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarInteger <em>DVar Integer</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarInteger
   * @generated
   */
  public Adapter createDVarIntegerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerStd <em>DVar Integer Std</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerStd
   * @generated
   */
  public Adapter createDVarIntegerStdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerNonStd <em>DVar Integer Non Std</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerNonStd
   * @generated
   */
  public Adapter createDVarIntegerNonStdAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVar <em>SVar</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVar
   * @generated
   */
  public Adapter createSVarAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarDSS <em>SVar DSS</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarDSS
   * @generated
   */
  public Adapter createSVarDSSAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarExpression <em>SVar Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarExpression
   * @generated
   */
  public Adapter createSVarExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum <em>SVar Sum</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum
   * @generated
   */
  public Adapter createSVarSumAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarTable <em>SVar Table</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarTable
   * @generated
   */
  public Adapter createSVarTableAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarCase <em>SVar Case</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarCase
   * @generated
   */
  public Adapter createSVarCaseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.CaseContent <em>Case Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.CaseContent
   * @generated
   */
  public Adapter createCaseContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumContent <em>Sum Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumContent
   * @generated
   */
  public Adapter createSumContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumHeader <em>Sum Header</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumHeader
   * @generated
   */
  public Adapter createSumHeaderAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ValueContent <em>Value Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ValueContent
   * @generated
   */
  public Adapter createValueContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TableContent <em>Table Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TableContent
   * @generated
   */
  public Adapter createTableContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.WhereItems <em>Where Items</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WhereItems
   * @generated
   */
  public Adapter createWhereItemsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Assignment <em>Assignment</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Assignment
   * @generated
   */
  public Adapter createAssignmentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TermSimple <em>Term Simple</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TermSimple
   * @generated
   */
  public Adapter createTermSimpleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LowerAndOrUpper <em>Lower And Or Upper</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.LowerAndOrUpper
   * @generated
   */
  public Adapter createLowerAndOrUpperAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.upperLower <em>upper Lower</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.upperLower
   * @generated
   */
  public Adapter createupperLowerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.lowerUpper <em>lower Upper</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.lowerUpper
   * @generated
   */
  public Adapter createlowerUpperAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Upper <em>Upper</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Upper
   * @generated
   */
  public Adapter createUpperAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Lower <em>Lower</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Lower
   * @generated
   */
  public Adapter createLowerAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Goal <em>Goal</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Goal
   * @generated
   */
  public Adapter createGoalAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCase <em>Goal Case</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCase
   * @generated
   */
  public Adapter createGoalCaseAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCaseContent <em>Goal Case Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCaseContent
   * @generated
   */
  public Adapter createGoalCaseContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalNoCaseContent <em>Goal No Case Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalNoCaseContent
   * @generated
   */
  public Adapter createGoalNoCaseContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent <em>Sub Content</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent
   * @generated
   */
  public Adapter createSubContentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsGtRhs <em>Lhs Gt Rhs</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsGtRhs
   * @generated
   */
  public Adapter createLhsGtRhsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs <em>Lhs Lt Rhs</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs
   * @generated
   */
  public Adapter createLhsLtRhsAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Penalty <em>Penalty</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Penalty
   * @generated
   */
  public Adapter createPenaltyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalSimple <em>Goal Simple</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalSimple
   * @generated
   */
  public Adapter createGoalSimpleAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Constraint <em>Constraint</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Constraint
   * @generated
   */
  public Adapter createConstraintAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Group <em>Group</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Group
   * @generated
   */
  public Adapter createGroupAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Model <em>Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Model
   * @generated
   */
  public Adapter createModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Initial <em>Initial</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Initial
   * @generated
   */
  public Adapter createInitialAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Sequence <em>Sequence</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Sequence
   * @generated
   */
  public Adapter createSequenceAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Condition <em>Condition</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Condition
   * @generated
   */
  public Adapter createConditionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogicalExpression <em>Logical Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogicalExpression
   * @generated
   */
  public Adapter createLogicalExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalUnary <em>Conditional Unary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalUnary
   * @generated
   */
  public Adapter createConditionalUnaryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalTerm <em>Conditional Term</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalTerm
   * @generated
   */
  public Adapter createConditionalTermAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression <em>Expression</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression
   * @generated
   */
  public Adapter createExpressionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Add <em>Add</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Add
   * @generated
   */
  public Adapter createAddAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply <em>Multiply</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply
   * @generated
   */
  public Adapter createMultiplyAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Unary <em>Unary</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Unary
   * @generated
   */
  public Adapter createUnaryAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Term <em>Term</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Term
   * @generated
   */
  public Adapter createTermAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Function <em>Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Function
   * @generated
   */
  public Adapter createFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction1 <em>External Function1</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction1
   * @generated
   */
  public Adapter createExternalFunction1Adapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction2 <em>External Function2</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction2
   * @generated
   */
  public Adapter createExternalFunction2Adapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArray <em>Trunk Time Array</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArray
   * @generated
   */
  public Adapter createTrunkTimeArrayAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArrayIndex <em>Trunk Time Array Index</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArrayIndex
   * @generated
   */
  public Adapter createTrunkTimeArrayIndexAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.MaxFunction <em>Max Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.MaxFunction
   * @generated
   */
  public Adapter createMaxFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.MinFunction <em>Min Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.MinFunction
   * @generated
   */
  public Adapter createMinFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.ModFunction <em>Mod Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.ModFunction
   * @generated
   */
  public Adapter createModFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IntFunction <em>Int Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.IntFunction
   * @generated
   */
  public Adapter createIntFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.AbsFunction <em>Abs Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.AbsFunction
   * @generated
   */
  public Adapter createAbsFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.RoundFunction <em>Round Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.RoundFunction
   * @generated
   */
  public Adapter createRoundFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.PowFunction <em>Pow Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.PowFunction
   * @generated
   */
  public Adapter createPowFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogFunction <em>Log Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogFunction
   * @generated
   */
  public Adapter createLogFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.SinFunction <em>Sin Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.SinFunction
   * @generated
   */
  public Adapter createSinFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.CosFunction <em>Cos Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.CosFunction
   * @generated
   */
  public Adapter createCosFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.TanFunction <em>Tan Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.TanFunction
   * @generated
   */
  public Adapter createTanFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.CotFunction <em>Cot Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.CotFunction
   * @generated
   */
  public Adapter createCotFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.AsinFunction <em>Asin Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.AsinFunction
   * @generated
   */
  public Adapter createAsinFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcosFunction <em>Acos Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcosFunction
   * @generated
   */
  public Adapter createAcosFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.AtanFunction <em>Atan Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.AtanFunction
   * @generated
   */
  public Adapter createAtanFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcotFunction <em>Acot Function</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcotFunction
   * @generated
   */
  public Adapter createAcotFunctionAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModel <em>Var Model</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModel
   * @generated
   */
  public Adapter createVarModelAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelStep <em>Var Model Step</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelStep
   * @generated
   */
  public Adapter createVarModelStepAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndex <em>Var Model Index</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndex
   * @generated
   */
  public Adapter createVarModelIndexAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndexStep <em>Var Model Index Step</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndexStep
   * @generated
   */
  public Adapter createVarModelIndexStepAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.Ident <em>Ident</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.Ident
   * @generated
   */
  public Adapter createIdentAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for an object of class '{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.IncludeFile <em>Include File</em>}'.
   * <!-- begin-user-doc -->
   * This default implementation returns null so that we can easily ignore cases;
   * it's useful to ignore a case when inheritance will catch all the cases anyway.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.IncludeFile
   * @generated
   */
  public Adapter createIncludeFileAdapter()
  {
    return null;
  }

  /**
   * Creates a new adapter for the default case.
   * <!-- begin-user-doc -->
   * This default implementation returns null.
   * <!-- end-user-doc -->
   * @return the new adapter.
   * @generated
   */
  public Adapter createEObjectAdapter()
  {
    return null;
  }

} //WreslEditorAdapterFactory
