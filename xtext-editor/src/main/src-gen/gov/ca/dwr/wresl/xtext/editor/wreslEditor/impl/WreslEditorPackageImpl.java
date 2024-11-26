/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.AbsFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcosFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.AcotFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Add;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Alias;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.AsinFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Assignment;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.AtanFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.CaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Condition;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConditionalUnary;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ConstDef;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Constraint;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.CosFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.CotFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVar;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarInteger;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerNonStd;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarIntegerStd;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarNonStd;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DVarStd;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DecisionVariable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Declaration;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.DvarDef;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseIfTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Expression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.External;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalDef;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction1;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ExternalFunction2;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Function;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Goal;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCase;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalCaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalNoCaseContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalSimple;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Group;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Ident;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfIncItems;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IncludeFile;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Initial;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IntFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsGtRhs;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LhsLtRhs;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogicalExpression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Lower;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LowerAndOrUpper;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.MaxFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.MinFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ModFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Model;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Multiply;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Objective;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Penalty;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.PowFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.RoundFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVar;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarCase;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarDSS;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarExpression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarSum;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SVarTable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Sequence;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SinFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.StateVariable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SubContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SumHeader;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.SvarDef;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TableContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TanFunction;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Term;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TermSimple;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TimeArraySize;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArray;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.TrunkTimeArrayIndex;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Unary;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Upper;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ValueContent;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModel;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndex;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelIndexStep;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.VarModelStep;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Variable;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WeightItem;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WhereItems;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorFactory;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEvaluator;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.lowerUpper;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.upperLower;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class WreslEditorPackageImpl extends EPackageImpl implements WreslEditorPackage
{
  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass wreslEvaluatorEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass patternEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass declarationEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass variableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass stateVariableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass decisionVariableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ifIncItemsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass ifTermEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass elseIfTermEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass elseTermEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass timeArraySizeEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass objectiveEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass weightItemEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass externalDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass svarDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dvarDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass constDefEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass aliasEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass externalEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarNonStdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarStdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarIntegerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarIntegerStdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass dVarIntegerNonStdEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarDSSEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarSumEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarTableEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sVarCaseEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass caseContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sumContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sumHeaderEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass valueContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tableContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass whereItemsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass assignmentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass termSimpleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lowerAndOrUpperEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass upperLowerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lowerUpperEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass upperEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lowerEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass goalEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass goalCaseEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass goalCaseContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass goalNoCaseContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass subContentEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lhsGtRhsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass lhsLtRhsEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass penaltyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass goalSimpleEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass constraintEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass groupEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass initialEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sequenceEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conditionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass logicalExpressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conditionalUnaryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass conditionalTermEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass expressionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass addEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass multiplyEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass unaryEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass termEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass functionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass externalFunction1EClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass externalFunction2EClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass trunkTimeArrayEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass trunkTimeArrayIndexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass maxFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass minFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass modFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass intFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass absFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass roundFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass powFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass logFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass sinFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass cosFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass tanFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass cotFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass asinFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass acosFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass atanFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass acotFunctionEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass varModelEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass varModelStepEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass varModelIndexEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass varModelIndexStepEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass identEClass = null;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private EClass includeFileEClass = null;

  /**
   * Creates an instance of the model <b>Package</b>, registered with
   * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
   * package URI value.
   * <p>Note: the correct way to create the package is via the static
   * factory method {@link #init init()}, which also performs
   * initialization of the package, or returns the registered package,
   * if one already exists.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see org.eclipse.emf.ecore.EPackage.Registry
   * @see gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage#eNS_URI
   * @see #init()
   * @generated
   */
  private WreslEditorPackageImpl()
  {
    super(eNS_URI, WreslEditorFactory.eINSTANCE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private static boolean isInited = false;

  /**
   * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
   * 
   * <p>This method is used to initialize {@link WreslEditorPackage#eINSTANCE} when that field is accessed.
   * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #eNS_URI
   * @see #createPackageContents()
   * @see #initializePackageContents()
   * @generated
   */
  public static WreslEditorPackage init()
  {
    if (isInited) return (WreslEditorPackage)EPackage.Registry.INSTANCE.getEPackage(WreslEditorPackage.eNS_URI);

    // Obtain or create and register package
    WreslEditorPackageImpl theWreslEditorPackage = (WreslEditorPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof WreslEditorPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new WreslEditorPackageImpl());

    isInited = true;

    // Create package meta-data objects
    theWreslEditorPackage.createPackageContents();

    // Initialize created meta-data
    theWreslEditorPackage.initializePackageContents();

    // Mark meta-data to indicate it can't be changed
    theWreslEditorPackage.freeze();

  
    // Update the registry and return the package
    EPackage.Registry.INSTANCE.put(WreslEditorPackage.eNS_URI, theWreslEditorPackage);
    return theWreslEditorPackage;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWreslEvaluator()
  {
    return wreslEvaluatorEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWreslEvaluator_Pattern()
  {
    return (EReference)wreslEvaluatorEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWreslEvaluator_Ifincitem()
  {
    return (EReference)wreslEvaluatorEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWreslEvaluator_Initial()
  {
    return (EReference)wreslEvaluatorEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWreslEvaluator_Sequence()
  {
    return (EReference)wreslEvaluatorEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWreslEvaluator_Model()
  {
    return (EReference)wreslEvaluatorEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPattern()
  {
    return patternEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getPattern_Local()
  {
    return (EAttribute)patternEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDeclaration()
  {
    return declarationEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDeclaration_Name()
  {
    return (EAttribute)declarationEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVariable()
  {
    return variableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVariable_Ref()
  {
    return (EReference)variableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getStateVariable()
  {
    return stateVariableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDecisionVariable()
  {
    return decisionVariableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDecisionVariable_Ta()
  {
    return (EReference)decisionVariableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIfIncItems()
  {
    return ifIncItemsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIfTerm()
  {
    return ifTermEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getIfTerm_Elseifterm()
  {
    return (EReference)ifTermEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getIfTerm_Elseterm()
  {
    return (EReference)ifTermEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getIfTerm_Logical()
  {
    return (EReference)ifTermEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getIfTerm_Pattern()
  {
    return (EReference)ifTermEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getElseIfTerm()
  {
    return elseIfTermEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getElseIfTerm_Logical()
  {
    return (EReference)elseIfTermEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getElseIfTerm_Pattern()
  {
    return (EReference)elseIfTermEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getElseTerm()
  {
    return elseTermEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getElseTerm_Pattern()
  {
    return (EReference)elseTermEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTimeArraySize()
  {
    return timeArraySizeEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTimeArraySize_Name()
  {
    return (EReference)timeArraySizeEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getObjective()
  {
    return objectiveEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getObjective_Name()
  {
    return (EAttribute)objectiveEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getObjective_Weights()
  {
    return (EReference)objectiveEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWeightItem()
  {
    return weightItemEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWeightItem_Ref()
  {
    return (EReference)weightItemEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWeightItem_Ta()
  {
    return (EReference)weightItemEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWeightItem_Expression()
  {
    return (EReference)weightItemEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExternalDef()
  {
    return externalDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalDef_Definition()
  {
    return (EReference)externalDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSvarDef()
  {
    return svarDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSvarDef_Ta()
  {
    return (EReference)svarDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSvarDef_Definition()
  {
    return (EReference)svarDefEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDvarDef()
  {
    return dvarDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDvarDef_Definition()
  {
    return (EReference)dvarDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConstDef()
  {
    return constDefEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConstDef_Definition()
  {
    return (EAttribute)constDefEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAlias()
  {
    return aliasEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAlias_Expression()
  {
    return (EReference)aliasEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAlias_Kind()
  {
    return (EAttribute)aliasEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getAlias_Units()
  {
    return (EAttribute)aliasEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExternal()
  {
    return externalEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getExternal_Name()
  {
    return (EAttribute)externalEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVar()
  {
    return dVarEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDVar_Kind()
  {
    return (EAttribute)dVarEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDVar_Units()
  {
    return (EAttribute)dVarEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVarNonStd()
  {
    return dVarNonStdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getDVarNonStd_LowerUpper()
  {
    return (EReference)dVarNonStdEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVarStd()
  {
    return dVarStdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVarInteger()
  {
    return dVarIntegerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVarIntegerStd()
  {
    return dVarIntegerStdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDVarIntegerStd_Kind()
  {
    return (EAttribute)dVarIntegerStdEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getDVarIntegerStd_Units()
  {
    return (EAttribute)dVarIntegerStdEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getDVarIntegerNonStd()
  {
    return dVarIntegerNonStdEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVar()
  {
    return sVarEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVarDSS()
  {
    return sVarDSSEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSVarDSS_BPart()
  {
    return (EAttribute)sVarDSSEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSVarDSS_Kind()
  {
    return (EAttribute)sVarDSSEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSVarDSS_Units()
  {
    return (EAttribute)sVarDSSEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSVarDSS_Convert()
  {
    return (EAttribute)sVarDSSEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVarExpression()
  {
    return sVarExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSVarExpression_Expression()
  {
    return (EReference)sVarExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVarSum()
  {
    return sVarSumEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSVarSum_SumContent()
  {
    return (EReference)sVarSumEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVarTable()
  {
    return sVarTableEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSVarTable_TableContent()
  {
    return (EReference)sVarTableEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSVarCase()
  {
    return sVarCaseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSVarCase_CaseContent()
  {
    return (EReference)sVarCaseEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCaseContent()
  {
    return caseContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getCaseContent_CaseName()
  {
    return (EAttribute)caseContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCaseContent_Condition()
  {
    return (EReference)caseContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCaseContent_Content()
  {
    return (EReference)caseContentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSumContent()
  {
    return sumContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSumContent_Header()
  {
    return (EReference)sumContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSumContent_Expression()
  {
    return (EReference)sumContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSumHeader()
  {
    return sumHeaderEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSumHeader_Expression1()
  {
    return (EReference)sumHeaderEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSumHeader_Expression2()
  {
    return (EReference)sumHeaderEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getValueContent()
  {
    return valueContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getValueContent_Expression()
  {
    return (EReference)valueContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTableContent()
  {
    return tableContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTableContent_TableName()
  {
    return (EAttribute)tableContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTableContent_From()
  {
    return (EAttribute)tableContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTableContent_Given()
  {
    return (EReference)tableContentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTableContent_Use()
  {
    return (EAttribute)tableContentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTableContent_Where()
  {
    return (EReference)tableContentEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getWhereItems()
  {
    return whereItemsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getWhereItems_Assignment()
  {
    return (EReference)whereItemsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAssignment()
  {
    return assignmentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAssignment_Term()
  {
    return (EReference)assignmentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAssignment_Expression()
  {
    return (EReference)assignmentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTermSimple()
  {
    return termSimpleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLowerAndOrUpper()
  {
    return lowerAndOrUpperEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLowerAndOrUpper_Kind()
  {
    return (EAttribute)lowerAndOrUpperEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getLowerAndOrUpper_Units()
  {
    return (EAttribute)lowerAndOrUpperEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLowerAndOrUpper_Upper()
  {
    return (EReference)lowerAndOrUpperEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLowerAndOrUpper_Lower()
  {
    return (EReference)lowerAndOrUpperEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getupperLower()
  {
    return upperLowerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getlowerUpper()
  {
    return lowerUpperEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUpper()
  {
    return upperEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getUpper_Expression()
  {
    return (EReference)upperEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLower()
  {
    return lowerEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLower_Expression()
  {
    return (EReference)lowerEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGoal()
  {
    return goalEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoal_Ta()
  {
    return (EReference)goalEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGoal_Name()
  {
    return (EAttribute)goalEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoal_Definition()
  {
    return (EReference)goalEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGoalCase()
  {
    return goalCaseEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCase_Lhs()
  {
    return (EReference)goalCaseEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCase_Content()
  {
    return (EReference)goalCaseEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCase_CaseContent()
  {
    return (EReference)goalCaseEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGoalCaseContent()
  {
    return goalCaseContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGoalCaseContent_CaseName()
  {
    return (EAttribute)goalCaseContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCaseContent_Condition()
  {
    return (EReference)goalCaseContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCaseContent_Rhs()
  {
    return (EReference)goalCaseContentEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalCaseContent_SubContent()
  {
    return (EReference)goalCaseContentEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGoalNoCaseContent()
  {
    return goalNoCaseContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalNoCaseContent_Rhs()
  {
    return (EReference)goalNoCaseContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalNoCaseContent_SubContent()
  {
    return (EReference)goalNoCaseContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSubContent()
  {
    return subContentEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubContent_Gt()
  {
    return (EReference)subContentEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSubContent_Lt()
  {
    return (EReference)subContentEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLhsGtRhs()
  {
    return lhsGtRhsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLhsGtRhs_Penalty()
  {
    return (EReference)lhsGtRhsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLhsLtRhs()
  {
    return lhsLtRhsEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLhsLtRhs_Penalty()
  {
    return (EReference)lhsLtRhsEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPenalty()
  {
    return penaltyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPenalty_Expression()
  {
    return (EReference)penaltyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGoalSimple()
  {
    return goalSimpleEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGoalSimple_Constraint()
  {
    return (EReference)goalSimpleEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConstraint()
  {
    return constraintEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConstraint_Lhs()
  {
    return (EReference)constraintEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getConstraint_Operator()
  {
    return (EAttribute)constraintEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConstraint_Rhs()
  {
    return (EReference)constraintEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getGroup()
  {
    return groupEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getGroup_Name()
  {
    return (EAttribute)groupEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_Pattern()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getGroup_Ifincitems()
  {
    return (EReference)groupEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModel()
  {
    return modelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getModel_Name()
  {
    return (EAttribute)modelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_Pattern()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModel_Ifincitems()
  {
    return (EReference)modelEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getInitial()
  {
    return initialEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getInitial_Pattern()
  {
    return (EReference)initialEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSequence()
  {
    return sequenceEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSequence_Name()
  {
    return (EAttribute)sequenceEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSequence_Model()
  {
    return (EReference)sequenceEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSequence_Condition()
  {
    return (EReference)sequenceEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getSequence_Order()
  {
    return (EAttribute)sequenceEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCondition()
  {
    return conditionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCondition_Logical()
  {
    return (EReference)conditionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLogicalExpression()
  {
    return logicalExpressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLogicalExpression_C1()
  {
    return (EReference)logicalExpressionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLogicalExpression_C2()
  {
    return (EReference)logicalExpressionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConditionalUnary()
  {
    return conditionalUnaryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getConditionalTerm()
  {
    return conditionalTermEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConditionalTerm_E1()
  {
    return (EReference)conditionalTermEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getConditionalTerm_E2()
  {
    return (EReference)conditionalTermEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExpression()
  {
    return expressionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAdd()
  {
    return addEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAdd_M1()
  {
    return (EReference)addEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAdd_M2()
  {
    return (EReference)addEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMultiply()
  {
    return multiplyEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMultiply_U1()
  {
    return (EReference)multiplyEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMultiply_U2()
  {
    return (EReference)multiplyEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getUnary()
  {
    return unaryEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTerm()
  {
    return termEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTerm_Ref()
  {
    return (EReference)termEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTerm_N()
  {
    return (EAttribute)termEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTerm_F()
  {
    return (EReference)termEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTerm_E()
  {
    return (EReference)termEClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getTerm_S()
  {
    return (EAttribute)termEClass.getEStructuralFeatures().get(4);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getFunction()
  {
    return functionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExternalFunction1()
  {
    return externalFunction1EClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalFunction1_Ref()
  {
    return (EReference)externalFunction1EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalFunction1_E1()
  {
    return (EReference)externalFunction1EClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalFunction1_E2()
  {
    return (EReference)externalFunction1EClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalFunction1_E0()
  {
    return (EReference)externalFunction1EClass.getEStructuralFeatures().get(3);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getExternalFunction2()
  {
    return externalFunction2EClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getExternalFunction2_Ref()
  {
    return (EReference)externalFunction2EClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTrunkTimeArray()
  {
    return trunkTimeArrayEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTrunkTimeArray_Ref()
  {
    return (EReference)trunkTimeArrayEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTrunkTimeArray_T1()
  {
    return (EReference)trunkTimeArrayEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTrunkTimeArray_T2()
  {
    return (EReference)trunkTimeArrayEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTrunkTimeArrayIndex()
  {
    return trunkTimeArrayIndexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTrunkTimeArrayIndex_Ref()
  {
    return (EReference)trunkTimeArrayIndexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMaxFunction()
  {
    return maxFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMaxFunction_E1()
  {
    return (EReference)maxFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMaxFunction_E2()
  {
    return (EReference)maxFunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getMinFunction()
  {
    return minFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMinFunction_E1()
  {
    return (EReference)minFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getMinFunction_E2()
  {
    return (EReference)minFunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getModFunction()
  {
    return modFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModFunction_E1()
  {
    return (EReference)modFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getModFunction_E2()
  {
    return (EReference)modFunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIntFunction()
  {
    return intFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getIntFunction_E()
  {
    return (EReference)intFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAbsFunction()
  {
    return absFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAbsFunction_E()
  {
    return (EReference)absFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getRoundFunction()
  {
    return roundFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getRoundFunction_E()
  {
    return (EReference)roundFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getPowFunction()
  {
    return powFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPowFunction_E1()
  {
    return (EReference)powFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getPowFunction_E2()
  {
    return (EReference)powFunctionEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getLogFunction()
  {
    return logFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getLogFunction_E()
  {
    return (EReference)logFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getSinFunction()
  {
    return sinFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getSinFunction_E()
  {
    return (EReference)sinFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCosFunction()
  {
    return cosFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCosFunction_E()
  {
    return (EReference)cosFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getTanFunction()
  {
    return tanFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getTanFunction_E()
  {
    return (EReference)tanFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getCotFunction()
  {
    return cotFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getCotFunction_E()
  {
    return (EReference)cotFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAsinFunction()
  {
    return asinFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAsinFunction_E()
  {
    return (EReference)asinFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAcosFunction()
  {
    return acosFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAcosFunction_E()
  {
    return (EReference)acosFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAtanFunction()
  {
    return atanFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAtanFunction_E()
  {
    return (EReference)atanFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getAcotFunction()
  {
    return acotFunctionEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getAcotFunction_E()
  {
    return (EReference)acotFunctionEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVarModel()
  {
    return varModelEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModel_Ref1()
  {
    return (EReference)varModelEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModel_Ref2()
  {
    return (EReference)varModelEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVarModelStep()
  {
    return varModelStepEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelStep_Ref1()
  {
    return (EReference)varModelStepEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelStep_Ref2()
  {
    return (EReference)varModelStepEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelStep_E()
  {
    return (EReference)varModelStepEClass.getEStructuralFeatures().get(2);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVarModelIndex()
  {
    return varModelIndexEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelIndex_Ref1()
  {
    return (EReference)varModelIndexEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getVarModelIndexStep()
  {
    return varModelIndexStepEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelIndexStep_Ref1()
  {
    return (EReference)varModelIndexStepEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EReference getVarModelIndexStep_E()
  {
    return (EReference)varModelIndexStepEClass.getEStructuralFeatures().get(1);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIdent()
  {
    return identEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIdent_Name()
  {
    return (EAttribute)identEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EClass getIncludeFile()
  {
    return includeFileEClass;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EAttribute getIncludeFile_File()
  {
    return (EAttribute)includeFileEClass.getEStructuralFeatures().get(0);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public WreslEditorFactory getWreslEditorFactory()
  {
    return (WreslEditorFactory)getEFactoryInstance();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isCreated = false;

  /**
   * Creates the meta-model objects for the package.  This method is
   * guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void createPackageContents()
  {
    if (isCreated) return;
    isCreated = true;

    // Create classes and their features
    wreslEvaluatorEClass = createEClass(WRESL_EVALUATOR);
    createEReference(wreslEvaluatorEClass, WRESL_EVALUATOR__PATTERN);
    createEReference(wreslEvaluatorEClass, WRESL_EVALUATOR__IFINCITEM);
    createEReference(wreslEvaluatorEClass, WRESL_EVALUATOR__INITIAL);
    createEReference(wreslEvaluatorEClass, WRESL_EVALUATOR__SEQUENCE);
    createEReference(wreslEvaluatorEClass, WRESL_EVALUATOR__MODEL);

    patternEClass = createEClass(PATTERN);
    createEAttribute(patternEClass, PATTERN__LOCAL);

    declarationEClass = createEClass(DECLARATION);
    createEAttribute(declarationEClass, DECLARATION__NAME);

    variableEClass = createEClass(VARIABLE);
    createEReference(variableEClass, VARIABLE__REF);

    stateVariableEClass = createEClass(STATE_VARIABLE);

    decisionVariableEClass = createEClass(DECISION_VARIABLE);
    createEReference(decisionVariableEClass, DECISION_VARIABLE__TA);

    ifIncItemsEClass = createEClass(IF_INC_ITEMS);

    ifTermEClass = createEClass(IF_TERM);
    createEReference(ifTermEClass, IF_TERM__ELSEIFTERM);
    createEReference(ifTermEClass, IF_TERM__ELSETERM);
    createEReference(ifTermEClass, IF_TERM__LOGICAL);
    createEReference(ifTermEClass, IF_TERM__PATTERN);

    elseIfTermEClass = createEClass(ELSE_IF_TERM);
    createEReference(elseIfTermEClass, ELSE_IF_TERM__LOGICAL);
    createEReference(elseIfTermEClass, ELSE_IF_TERM__PATTERN);

    elseTermEClass = createEClass(ELSE_TERM);
    createEReference(elseTermEClass, ELSE_TERM__PATTERN);

    timeArraySizeEClass = createEClass(TIME_ARRAY_SIZE);
    createEReference(timeArraySizeEClass, TIME_ARRAY_SIZE__NAME);

    objectiveEClass = createEClass(OBJECTIVE);
    createEAttribute(objectiveEClass, OBJECTIVE__NAME);
    createEReference(objectiveEClass, OBJECTIVE__WEIGHTS);

    weightItemEClass = createEClass(WEIGHT_ITEM);
    createEReference(weightItemEClass, WEIGHT_ITEM__REF);
    createEReference(weightItemEClass, WEIGHT_ITEM__TA);
    createEReference(weightItemEClass, WEIGHT_ITEM__EXPRESSION);

    externalDefEClass = createEClass(EXTERNAL_DEF);
    createEReference(externalDefEClass, EXTERNAL_DEF__DEFINITION);

    svarDefEClass = createEClass(SVAR_DEF);
    createEReference(svarDefEClass, SVAR_DEF__TA);
    createEReference(svarDefEClass, SVAR_DEF__DEFINITION);

    dvarDefEClass = createEClass(DVAR_DEF);
    createEReference(dvarDefEClass, DVAR_DEF__DEFINITION);

    constDefEClass = createEClass(CONST_DEF);
    createEAttribute(constDefEClass, CONST_DEF__DEFINITION);

    aliasEClass = createEClass(ALIAS);
    createEReference(aliasEClass, ALIAS__EXPRESSION);
    createEAttribute(aliasEClass, ALIAS__KIND);
    createEAttribute(aliasEClass, ALIAS__UNITS);

    externalEClass = createEClass(EXTERNAL);
    createEAttribute(externalEClass, EXTERNAL__NAME);

    dVarEClass = createEClass(DVAR);
    createEAttribute(dVarEClass, DVAR__KIND);
    createEAttribute(dVarEClass, DVAR__UNITS);

    dVarNonStdEClass = createEClass(DVAR_NON_STD);
    createEReference(dVarNonStdEClass, DVAR_NON_STD__LOWER_UPPER);

    dVarStdEClass = createEClass(DVAR_STD);

    dVarIntegerEClass = createEClass(DVAR_INTEGER);

    dVarIntegerStdEClass = createEClass(DVAR_INTEGER_STD);
    createEAttribute(dVarIntegerStdEClass, DVAR_INTEGER_STD__KIND);
    createEAttribute(dVarIntegerStdEClass, DVAR_INTEGER_STD__UNITS);

    dVarIntegerNonStdEClass = createEClass(DVAR_INTEGER_NON_STD);

    sVarEClass = createEClass(SVAR);

    sVarDSSEClass = createEClass(SVAR_DSS);
    createEAttribute(sVarDSSEClass, SVAR_DSS__BPART);
    createEAttribute(sVarDSSEClass, SVAR_DSS__KIND);
    createEAttribute(sVarDSSEClass, SVAR_DSS__UNITS);
    createEAttribute(sVarDSSEClass, SVAR_DSS__CONVERT);

    sVarExpressionEClass = createEClass(SVAR_EXPRESSION);
    createEReference(sVarExpressionEClass, SVAR_EXPRESSION__EXPRESSION);

    sVarSumEClass = createEClass(SVAR_SUM);
    createEReference(sVarSumEClass, SVAR_SUM__SUM_CONTENT);

    sVarTableEClass = createEClass(SVAR_TABLE);
    createEReference(sVarTableEClass, SVAR_TABLE__TABLE_CONTENT);

    sVarCaseEClass = createEClass(SVAR_CASE);
    createEReference(sVarCaseEClass, SVAR_CASE__CASE_CONTENT);

    caseContentEClass = createEClass(CASE_CONTENT);
    createEAttribute(caseContentEClass, CASE_CONTENT__CASE_NAME);
    createEReference(caseContentEClass, CASE_CONTENT__CONDITION);
    createEReference(caseContentEClass, CASE_CONTENT__CONTENT);

    sumContentEClass = createEClass(SUM_CONTENT);
    createEReference(sumContentEClass, SUM_CONTENT__HEADER);
    createEReference(sumContentEClass, SUM_CONTENT__EXPRESSION);

    sumHeaderEClass = createEClass(SUM_HEADER);
    createEReference(sumHeaderEClass, SUM_HEADER__EXPRESSION1);
    createEReference(sumHeaderEClass, SUM_HEADER__EXPRESSION2);

    valueContentEClass = createEClass(VALUE_CONTENT);
    createEReference(valueContentEClass, VALUE_CONTENT__EXPRESSION);

    tableContentEClass = createEClass(TABLE_CONTENT);
    createEAttribute(tableContentEClass, TABLE_CONTENT__TABLE_NAME);
    createEAttribute(tableContentEClass, TABLE_CONTENT__FROM);
    createEReference(tableContentEClass, TABLE_CONTENT__GIVEN);
    createEAttribute(tableContentEClass, TABLE_CONTENT__USE);
    createEReference(tableContentEClass, TABLE_CONTENT__WHERE);

    whereItemsEClass = createEClass(WHERE_ITEMS);
    createEReference(whereItemsEClass, WHERE_ITEMS__ASSIGNMENT);

    assignmentEClass = createEClass(ASSIGNMENT);
    createEReference(assignmentEClass, ASSIGNMENT__TERM);
    createEReference(assignmentEClass, ASSIGNMENT__EXPRESSION);

    termSimpleEClass = createEClass(TERM_SIMPLE);

    lowerAndOrUpperEClass = createEClass(LOWER_AND_OR_UPPER);
    createEAttribute(lowerAndOrUpperEClass, LOWER_AND_OR_UPPER__KIND);
    createEAttribute(lowerAndOrUpperEClass, LOWER_AND_OR_UPPER__UNITS);
    createEReference(lowerAndOrUpperEClass, LOWER_AND_OR_UPPER__UPPER);
    createEReference(lowerAndOrUpperEClass, LOWER_AND_OR_UPPER__LOWER);

    upperLowerEClass = createEClass(UPPER_LOWER);

    lowerUpperEClass = createEClass(LOWER_UPPER);

    upperEClass = createEClass(UPPER);
    createEReference(upperEClass, UPPER__EXPRESSION);

    lowerEClass = createEClass(LOWER);
    createEReference(lowerEClass, LOWER__EXPRESSION);

    goalEClass = createEClass(GOAL);
    createEReference(goalEClass, GOAL__TA);
    createEAttribute(goalEClass, GOAL__NAME);
    createEReference(goalEClass, GOAL__DEFINITION);

    goalCaseEClass = createEClass(GOAL_CASE);
    createEReference(goalCaseEClass, GOAL_CASE__LHS);
    createEReference(goalCaseEClass, GOAL_CASE__CONTENT);
    createEReference(goalCaseEClass, GOAL_CASE__CASE_CONTENT);

    goalCaseContentEClass = createEClass(GOAL_CASE_CONTENT);
    createEAttribute(goalCaseContentEClass, GOAL_CASE_CONTENT__CASE_NAME);
    createEReference(goalCaseContentEClass, GOAL_CASE_CONTENT__CONDITION);
    createEReference(goalCaseContentEClass, GOAL_CASE_CONTENT__RHS);
    createEReference(goalCaseContentEClass, GOAL_CASE_CONTENT__SUB_CONTENT);

    goalNoCaseContentEClass = createEClass(GOAL_NO_CASE_CONTENT);
    createEReference(goalNoCaseContentEClass, GOAL_NO_CASE_CONTENT__RHS);
    createEReference(goalNoCaseContentEClass, GOAL_NO_CASE_CONTENT__SUB_CONTENT);

    subContentEClass = createEClass(SUB_CONTENT);
    createEReference(subContentEClass, SUB_CONTENT__GT);
    createEReference(subContentEClass, SUB_CONTENT__LT);

    lhsGtRhsEClass = createEClass(LHS_GT_RHS);
    createEReference(lhsGtRhsEClass, LHS_GT_RHS__PENALTY);

    lhsLtRhsEClass = createEClass(LHS_LT_RHS);
    createEReference(lhsLtRhsEClass, LHS_LT_RHS__PENALTY);

    penaltyEClass = createEClass(PENALTY);
    createEReference(penaltyEClass, PENALTY__EXPRESSION);

    goalSimpleEClass = createEClass(GOAL_SIMPLE);
    createEReference(goalSimpleEClass, GOAL_SIMPLE__CONSTRAINT);

    constraintEClass = createEClass(CONSTRAINT);
    createEReference(constraintEClass, CONSTRAINT__LHS);
    createEAttribute(constraintEClass, CONSTRAINT__OPERATOR);
    createEReference(constraintEClass, CONSTRAINT__RHS);

    groupEClass = createEClass(GROUP);
    createEAttribute(groupEClass, GROUP__NAME);
    createEReference(groupEClass, GROUP__PATTERN);
    createEReference(groupEClass, GROUP__IFINCITEMS);

    modelEClass = createEClass(MODEL);
    createEAttribute(modelEClass, MODEL__NAME);
    createEReference(modelEClass, MODEL__PATTERN);
    createEReference(modelEClass, MODEL__IFINCITEMS);

    initialEClass = createEClass(INITIAL);
    createEReference(initialEClass, INITIAL__PATTERN);

    sequenceEClass = createEClass(SEQUENCE);
    createEAttribute(sequenceEClass, SEQUENCE__NAME);
    createEReference(sequenceEClass, SEQUENCE__MODEL);
    createEReference(sequenceEClass, SEQUENCE__CONDITION);
    createEAttribute(sequenceEClass, SEQUENCE__ORDER);

    conditionEClass = createEClass(CONDITION);
    createEReference(conditionEClass, CONDITION__LOGICAL);

    logicalExpressionEClass = createEClass(LOGICAL_EXPRESSION);
    createEReference(logicalExpressionEClass, LOGICAL_EXPRESSION__C1);
    createEReference(logicalExpressionEClass, LOGICAL_EXPRESSION__C2);

    conditionalUnaryEClass = createEClass(CONDITIONAL_UNARY);

    conditionalTermEClass = createEClass(CONDITIONAL_TERM);
    createEReference(conditionalTermEClass, CONDITIONAL_TERM__E1);
    createEReference(conditionalTermEClass, CONDITIONAL_TERM__E2);

    expressionEClass = createEClass(EXPRESSION);

    addEClass = createEClass(ADD);
    createEReference(addEClass, ADD__M1);
    createEReference(addEClass, ADD__M2);

    multiplyEClass = createEClass(MULTIPLY);
    createEReference(multiplyEClass, MULTIPLY__U1);
    createEReference(multiplyEClass, MULTIPLY__U2);

    unaryEClass = createEClass(UNARY);

    termEClass = createEClass(TERM);
    createEReference(termEClass, TERM__REF);
    createEAttribute(termEClass, TERM__N);
    createEReference(termEClass, TERM__F);
    createEReference(termEClass, TERM__E);
    createEAttribute(termEClass, TERM__S);

    functionEClass = createEClass(FUNCTION);

    externalFunction1EClass = createEClass(EXTERNAL_FUNCTION1);
    createEReference(externalFunction1EClass, EXTERNAL_FUNCTION1__REF);
    createEReference(externalFunction1EClass, EXTERNAL_FUNCTION1__E1);
    createEReference(externalFunction1EClass, EXTERNAL_FUNCTION1__E2);
    createEReference(externalFunction1EClass, EXTERNAL_FUNCTION1__E0);

    externalFunction2EClass = createEClass(EXTERNAL_FUNCTION2);
    createEReference(externalFunction2EClass, EXTERNAL_FUNCTION2__REF);

    trunkTimeArrayEClass = createEClass(TRUNK_TIME_ARRAY);
    createEReference(trunkTimeArrayEClass, TRUNK_TIME_ARRAY__REF);
    createEReference(trunkTimeArrayEClass, TRUNK_TIME_ARRAY__T1);
    createEReference(trunkTimeArrayEClass, TRUNK_TIME_ARRAY__T2);

    trunkTimeArrayIndexEClass = createEClass(TRUNK_TIME_ARRAY_INDEX);
    createEReference(trunkTimeArrayIndexEClass, TRUNK_TIME_ARRAY_INDEX__REF);

    maxFunctionEClass = createEClass(MAX_FUNCTION);
    createEReference(maxFunctionEClass, MAX_FUNCTION__E1);
    createEReference(maxFunctionEClass, MAX_FUNCTION__E2);

    minFunctionEClass = createEClass(MIN_FUNCTION);
    createEReference(minFunctionEClass, MIN_FUNCTION__E1);
    createEReference(minFunctionEClass, MIN_FUNCTION__E2);

    modFunctionEClass = createEClass(MOD_FUNCTION);
    createEReference(modFunctionEClass, MOD_FUNCTION__E1);
    createEReference(modFunctionEClass, MOD_FUNCTION__E2);

    intFunctionEClass = createEClass(INT_FUNCTION);
    createEReference(intFunctionEClass, INT_FUNCTION__E);

    absFunctionEClass = createEClass(ABS_FUNCTION);
    createEReference(absFunctionEClass, ABS_FUNCTION__E);

    roundFunctionEClass = createEClass(ROUND_FUNCTION);
    createEReference(roundFunctionEClass, ROUND_FUNCTION__E);

    powFunctionEClass = createEClass(POW_FUNCTION);
    createEReference(powFunctionEClass, POW_FUNCTION__E1);
    createEReference(powFunctionEClass, POW_FUNCTION__E2);

    logFunctionEClass = createEClass(LOG_FUNCTION);
    createEReference(logFunctionEClass, LOG_FUNCTION__E);

    sinFunctionEClass = createEClass(SIN_FUNCTION);
    createEReference(sinFunctionEClass, SIN_FUNCTION__E);

    cosFunctionEClass = createEClass(COS_FUNCTION);
    createEReference(cosFunctionEClass, COS_FUNCTION__E);

    tanFunctionEClass = createEClass(TAN_FUNCTION);
    createEReference(tanFunctionEClass, TAN_FUNCTION__E);

    cotFunctionEClass = createEClass(COT_FUNCTION);
    createEReference(cotFunctionEClass, COT_FUNCTION__E);

    asinFunctionEClass = createEClass(ASIN_FUNCTION);
    createEReference(asinFunctionEClass, ASIN_FUNCTION__E);

    acosFunctionEClass = createEClass(ACOS_FUNCTION);
    createEReference(acosFunctionEClass, ACOS_FUNCTION__E);

    atanFunctionEClass = createEClass(ATAN_FUNCTION);
    createEReference(atanFunctionEClass, ATAN_FUNCTION__E);

    acotFunctionEClass = createEClass(ACOT_FUNCTION);
    createEReference(acotFunctionEClass, ACOT_FUNCTION__E);

    varModelEClass = createEClass(VAR_MODEL);
    createEReference(varModelEClass, VAR_MODEL__REF1);
    createEReference(varModelEClass, VAR_MODEL__REF2);

    varModelStepEClass = createEClass(VAR_MODEL_STEP);
    createEReference(varModelStepEClass, VAR_MODEL_STEP__REF1);
    createEReference(varModelStepEClass, VAR_MODEL_STEP__REF2);
    createEReference(varModelStepEClass, VAR_MODEL_STEP__E);

    varModelIndexEClass = createEClass(VAR_MODEL_INDEX);
    createEReference(varModelIndexEClass, VAR_MODEL_INDEX__REF1);

    varModelIndexStepEClass = createEClass(VAR_MODEL_INDEX_STEP);
    createEReference(varModelIndexStepEClass, VAR_MODEL_INDEX_STEP__REF1);
    createEReference(varModelIndexStepEClass, VAR_MODEL_INDEX_STEP__E);

    identEClass = createEClass(IDENT);
    createEAttribute(identEClass, IDENT__NAME);

    includeFileEClass = createEClass(INCLUDE_FILE);
    createEAttribute(includeFileEClass, INCLUDE_FILE__FILE);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  private boolean isInitialized = false;

  /**
   * Complete the initialization of the package and its meta-model.  This
   * method is guarded to have no affect on any invocation but its first.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void initializePackageContents()
  {
    if (isInitialized) return;
    isInitialized = true;

    // Initialize package
    setName(eNAME);
    setNsPrefix(eNS_PREFIX);
    setNsURI(eNS_URI);

    // Create type parameters

    // Set bounds for type parameters

    // Add supertypes to classes
    declarationEClass.getESuperTypes().add(this.getWreslEvaluator());
    variableEClass.getESuperTypes().add(this.getPattern());
    stateVariableEClass.getESuperTypes().add(this.getVariable());
    decisionVariableEClass.getESuperTypes().add(this.getVariable());
    ifTermEClass.getESuperTypes().add(this.getIfIncItems());
    objectiveEClass.getESuperTypes().add(this.getPattern());
    externalDefEClass.getESuperTypes().add(this.getVariable());
    svarDefEClass.getESuperTypes().add(this.getStateVariable());
    dvarDefEClass.getESuperTypes().add(this.getDecisionVariable());
    constDefEClass.getESuperTypes().add(this.getStateVariable());
    aliasEClass.getESuperTypes().add(this.getDecisionVariable());
    dVarNonStdEClass.getESuperTypes().add(this.getDVar());
    dVarStdEClass.getESuperTypes().add(this.getDVar());
    dVarIntegerStdEClass.getESuperTypes().add(this.getDVarInteger());
    dVarIntegerNonStdEClass.getESuperTypes().add(this.getDVarInteger());
    sVarDSSEClass.getESuperTypes().add(this.getSVar());
    sVarExpressionEClass.getESuperTypes().add(this.getSVar());
    sVarSumEClass.getESuperTypes().add(this.getSVar());
    sVarTableEClass.getESuperTypes().add(this.getSVar());
    sVarCaseEClass.getESuperTypes().add(this.getSVar());
    sumContentEClass.getESuperTypes().add(this.getFunction());
    lowerAndOrUpperEClass.getESuperTypes().add(this.getDVarIntegerNonStd());
    upperLowerEClass.getESuperTypes().add(this.getLowerAndOrUpper());
    lowerUpperEClass.getESuperTypes().add(this.getLowerAndOrUpper());
    goalEClass.getESuperTypes().add(this.getPattern());
    logicalExpressionEClass.getESuperTypes().add(this.getConditionalTerm());
    conditionalTermEClass.getESuperTypes().add(this.getConditionalUnary());
    addEClass.getESuperTypes().add(this.getExpression());
    termEClass.getESuperTypes().add(this.getUnary());
    functionEClass.getESuperTypes().add(this.getTermSimple());
    externalFunction1EClass.getESuperTypes().add(this.getFunction());
    externalFunction2EClass.getESuperTypes().add(this.getFunction());
    maxFunctionEClass.getESuperTypes().add(this.getFunction());
    minFunctionEClass.getESuperTypes().add(this.getFunction());
    modFunctionEClass.getESuperTypes().add(this.getFunction());
    intFunctionEClass.getESuperTypes().add(this.getFunction());
    absFunctionEClass.getESuperTypes().add(this.getFunction());
    roundFunctionEClass.getESuperTypes().add(this.getFunction());
    powFunctionEClass.getESuperTypes().add(this.getFunction());
    logFunctionEClass.getESuperTypes().add(this.getFunction());
    sinFunctionEClass.getESuperTypes().add(this.getFunction());
    cosFunctionEClass.getESuperTypes().add(this.getFunction());
    tanFunctionEClass.getESuperTypes().add(this.getFunction());
    cotFunctionEClass.getESuperTypes().add(this.getFunction());
    asinFunctionEClass.getESuperTypes().add(this.getFunction());
    acosFunctionEClass.getESuperTypes().add(this.getFunction());
    atanFunctionEClass.getESuperTypes().add(this.getFunction());
    acotFunctionEClass.getESuperTypes().add(this.getFunction());
    varModelEClass.getESuperTypes().add(this.getFunction());
    varModelStepEClass.getESuperTypes().add(this.getFunction());
    varModelIndexEClass.getESuperTypes().add(this.getFunction());
    varModelIndexStepEClass.getESuperTypes().add(this.getFunction());
    includeFileEClass.getESuperTypes().add(this.getPattern());

    // Initialize classes and features; add operations and parameters
    initEClass(wreslEvaluatorEClass, WreslEvaluator.class, "WreslEvaluator", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWreslEvaluator_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, WreslEvaluator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWreslEvaluator_Ifincitem(), this.getIfIncItems(), null, "ifincitem", null, 0, -1, WreslEvaluator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWreslEvaluator_Initial(), this.getInitial(), null, "initial", null, 0, 1, WreslEvaluator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWreslEvaluator_Sequence(), this.getSequence(), null, "sequence", null, 0, -1, WreslEvaluator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWreslEvaluator_Model(), ecorePackage.getEObject(), null, "model", null, 0, -1, WreslEvaluator.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(patternEClass, Pattern.class, "Pattern", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getPattern_Local(), ecorePackage.getEBoolean(), "local", null, 0, 1, Pattern.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(declarationEClass, Declaration.class, "Declaration", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDeclaration_Name(), ecorePackage.getEString(), "name", null, 0, 1, Declaration.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(variableEClass, Variable.class, "Variable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVariable_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, Variable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(stateVariableEClass, StateVariable.class, "StateVariable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(decisionVariableEClass, DecisionVariable.class, "DecisionVariable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDecisionVariable_Ta(), this.getTimeArraySize(), null, "ta", null, 0, 1, DecisionVariable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(ifIncItemsEClass, IfIncItems.class, "IfIncItems", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(ifTermEClass, IfTerm.class, "IfTerm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIfTerm_Elseifterm(), this.getElseIfTerm(), null, "elseifterm", null, 0, 1, IfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getIfTerm_Elseterm(), this.getElseTerm(), null, "elseterm", null, 0, 1, IfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getIfTerm_Logical(), this.getLogicalExpression(), null, "logical", null, 0, 1, IfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getIfTerm_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, IfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(elseIfTermEClass, ElseIfTerm.class, "ElseIfTerm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getElseIfTerm_Logical(), this.getLogicalExpression(), null, "logical", null, 0, -1, ElseIfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getElseIfTerm_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, ElseIfTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(elseTermEClass, ElseTerm.class, "ElseTerm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getElseTerm_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, ElseTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(timeArraySizeEClass, TimeArraySize.class, "TimeArraySize", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTimeArraySize_Name(), this.getDeclaration(), null, "name", null, 0, 1, TimeArraySize.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(objectiveEClass, Objective.class, "Objective", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getObjective_Name(), ecorePackage.getEString(), "name", null, 0, 1, Objective.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getObjective_Weights(), this.getWeightItem(), null, "weights", null, 0, -1, Objective.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(weightItemEClass, WeightItem.class, "WeightItem", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWeightItem_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, WeightItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWeightItem_Ta(), this.getTimeArraySize(), null, "ta", null, 0, 1, WeightItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getWeightItem_Expression(), this.getExpression(), null, "expression", null, 0, 1, WeightItem.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(externalDefEClass, ExternalDef.class, "ExternalDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExternalDef_Definition(), this.getExternal(), null, "definition", null, 0, 1, ExternalDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(svarDefEClass, SvarDef.class, "SvarDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSvarDef_Ta(), this.getTimeArraySize(), null, "ta", null, 0, 1, SvarDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSvarDef_Definition(), this.getSVar(), null, "definition", null, 0, 1, SvarDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dvarDefEClass, DvarDef.class, "DvarDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDvarDef_Definition(), ecorePackage.getEObject(), null, "definition", null, 0, 1, DvarDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(constDefEClass, ConstDef.class, "ConstDef", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getConstDef_Definition(), ecorePackage.getEString(), "definition", null, 0, 1, ConstDef.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(aliasEClass, Alias.class, "Alias", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAlias_Expression(), this.getExpression(), null, "expression", null, 0, 1, Alias.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAlias_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, Alias.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getAlias_Units(), ecorePackage.getEString(), "units", null, 0, 1, Alias.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(externalEClass, External.class, "External", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getExternal_Name(), ecorePackage.getEString(), "name", null, 0, 1, External.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dVarEClass, DVar.class, "DVar", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDVar_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, DVar.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDVar_Units(), ecorePackage.getEString(), "units", null, 0, 1, DVar.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dVarNonStdEClass, DVarNonStd.class, "DVarNonStd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getDVarNonStd_LowerUpper(), this.getLowerAndOrUpper(), null, "lowerUpper", null, 0, 1, DVarNonStd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dVarStdEClass, DVarStd.class, "DVarStd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(dVarIntegerEClass, DVarInteger.class, "DVarInteger", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(dVarIntegerStdEClass, DVarIntegerStd.class, "DVarIntegerStd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getDVarIntegerStd_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, DVarIntegerStd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getDVarIntegerStd_Units(), ecorePackage.getEString(), "units", null, 0, 1, DVarIntegerStd.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(dVarIntegerNonStdEClass, DVarIntegerNonStd.class, "DVarIntegerNonStd", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(sVarEClass, SVar.class, "SVar", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(sVarDSSEClass, SVarDSS.class, "SVarDSS", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSVarDSS_BPart(), ecorePackage.getEString(), "bPart", null, 0, 1, SVarDSS.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSVarDSS_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, SVarDSS.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSVarDSS_Units(), ecorePackage.getEString(), "units", null, 0, 1, SVarDSS.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSVarDSS_Convert(), ecorePackage.getEString(), "convert", null, 0, 1, SVarDSS.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sVarExpressionEClass, SVarExpression.class, "SVarExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSVarExpression_Expression(), this.getExpression(), null, "expression", null, 0, 1, SVarExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sVarSumEClass, SVarSum.class, "SVarSum", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSVarSum_SumContent(), this.getSumContent(), null, "sumContent", null, 0, 1, SVarSum.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sVarTableEClass, SVarTable.class, "SVarTable", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSVarTable_TableContent(), this.getTableContent(), null, "tableContent", null, 0, 1, SVarTable.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sVarCaseEClass, SVarCase.class, "SVarCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSVarCase_CaseContent(), this.getCaseContent(), null, "caseContent", null, 0, -1, SVarCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(caseContentEClass, CaseContent.class, "CaseContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getCaseContent_CaseName(), ecorePackage.getEString(), "caseName", null, 0, 1, CaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCaseContent_Condition(), this.getCondition(), null, "condition", null, 0, 1, CaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getCaseContent_Content(), ecorePackage.getEObject(), null, "content", null, 0, 1, CaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sumContentEClass, SumContent.class, "SumContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSumContent_Header(), this.getSumHeader(), null, "header", null, 0, 1, SumContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSumContent_Expression(), this.getExpression(), null, "expression", null, 0, 1, SumContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sumHeaderEClass, SumHeader.class, "SumHeader", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSumHeader_Expression1(), this.getExpression(), null, "expression1", null, 0, 1, SumHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSumHeader_Expression2(), this.getExpression(), null, "expression2", null, 0, 1, SumHeader.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(valueContentEClass, ValueContent.class, "ValueContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getValueContent_Expression(), this.getExpression(), null, "expression", null, 0, 1, ValueContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(tableContentEClass, TableContent.class, "TableContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getTableContent_TableName(), ecorePackage.getEString(), "tableName", null, 0, 1, TableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTableContent_From(), ecorePackage.getEString(), "from", null, 0, 1, TableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTableContent_Given(), this.getAssignment(), null, "given", null, 0, 1, TableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTableContent_Use(), ecorePackage.getEString(), "use", null, 0, 1, TableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTableContent_Where(), this.getWhereItems(), null, "where", null, 0, 1, TableContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(whereItemsEClass, WhereItems.class, "WhereItems", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getWhereItems_Assignment(), this.getAssignment(), null, "assignment", null, 0, -1, WhereItems.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(assignmentEClass, Assignment.class, "Assignment", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAssignment_Term(), this.getTermSimple(), null, "term", null, 0, 1, Assignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAssignment_Expression(), this.getExpression(), null, "expression", null, 0, 1, Assignment.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(termSimpleEClass, TermSimple.class, "TermSimple", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(lowerAndOrUpperEClass, LowerAndOrUpper.class, "LowerAndOrUpper", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getLowerAndOrUpper_Kind(), ecorePackage.getEString(), "kind", null, 0, 1, LowerAndOrUpper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getLowerAndOrUpper_Units(), ecorePackage.getEString(), "units", null, 0, 1, LowerAndOrUpper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getLowerAndOrUpper_Upper(), this.getUpper(), null, "upper", null, 0, 1, LowerAndOrUpper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getLowerAndOrUpper_Lower(), this.getLower(), null, "lower", null, 0, 1, LowerAndOrUpper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(upperLowerEClass, upperLower.class, "upperLower", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(lowerUpperEClass, lowerUpper.class, "lowerUpper", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(upperEClass, Upper.class, "Upper", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getUpper_Expression(), this.getExpression(), null, "expression", null, 0, 1, Upper.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lowerEClass, Lower.class, "Lower", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLower_Expression(), this.getExpression(), null, "expression", null, 0, 1, Lower.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(goalEClass, Goal.class, "Goal", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGoal_Ta(), this.getTimeArraySize(), null, "ta", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getGoal_Name(), ecorePackage.getEString(), "name", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoal_Definition(), ecorePackage.getEObject(), null, "definition", null, 0, 1, Goal.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(goalCaseEClass, GoalCase.class, "GoalCase", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGoalCase_Lhs(), this.getExpression(), null, "lhs", null, 0, 1, GoalCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalCase_Content(), this.getGoalNoCaseContent(), null, "content", null, 0, 1, GoalCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalCase_CaseContent(), this.getGoalCaseContent(), null, "caseContent", null, 0, -1, GoalCase.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(goalCaseContentEClass, GoalCaseContent.class, "GoalCaseContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGoalCaseContent_CaseName(), ecorePackage.getEString(), "caseName", null, 0, 1, GoalCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalCaseContent_Condition(), this.getCondition(), null, "condition", null, 0, 1, GoalCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalCaseContent_Rhs(), this.getExpression(), null, "rhs", null, 0, 1, GoalCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalCaseContent_SubContent(), this.getSubContent(), null, "subContent", null, 0, 1, GoalCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(goalNoCaseContentEClass, GoalNoCaseContent.class, "GoalNoCaseContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGoalNoCaseContent_Rhs(), this.getExpression(), null, "rhs", null, 0, 1, GoalNoCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGoalNoCaseContent_SubContent(), this.getSubContent(), null, "subContent", null, 0, 1, GoalNoCaseContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(subContentEClass, SubContent.class, "SubContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSubContent_Gt(), this.getLhsGtRhs(), null, "gt", null, 0, 1, SubContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSubContent_Lt(), this.getLhsLtRhs(), null, "lt", null, 0, 1, SubContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lhsGtRhsEClass, LhsGtRhs.class, "LhsGtRhs", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLhsGtRhs_Penalty(), this.getPenalty(), null, "penalty", null, 0, 1, LhsGtRhs.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(lhsLtRhsEClass, LhsLtRhs.class, "LhsLtRhs", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLhsLtRhs_Penalty(), this.getPenalty(), null, "penalty", null, 0, 1, LhsLtRhs.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(penaltyEClass, Penalty.class, "Penalty", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPenalty_Expression(), this.getExpression(), null, "expression", null, 0, 1, Penalty.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(goalSimpleEClass, GoalSimple.class, "GoalSimple", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getGoalSimple_Constraint(), this.getConstraint(), null, "constraint", null, 0, 1, GoalSimple.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(constraintEClass, Constraint.class, "Constraint", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConstraint_Lhs(), this.getExpression(), null, "lhs", null, 0, 1, Constraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getConstraint_Operator(), ecorePackage.getEString(), "operator", null, 0, 1, Constraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConstraint_Rhs(), this.getExpression(), null, "rhs", null, 0, 1, Constraint.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(groupEClass, Group.class, "Group", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getGroup_Name(), ecorePackage.getEString(), "name", null, 0, 1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGroup_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getGroup_Ifincitems(), this.getIfIncItems(), null, "ifincitems", null, 0, -1, Group.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(modelEClass, Model.class, "Model", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getModel_Name(), ecorePackage.getEString(), "name", null, 0, 1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModel_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModel_Ifincitems(), this.getIfIncItems(), null, "ifincitems", null, 0, -1, Model.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(initialEClass, Initial.class, "Initial", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getInitial_Pattern(), this.getPattern(), null, "pattern", null, 0, -1, Initial.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sequenceEClass, Sequence.class, "Sequence", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getSequence_Name(), ecorePackage.getEString(), "name", null, 0, 1, Sequence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSequence_Model(), this.getModel(), null, "model", null, 0, 1, Sequence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getSequence_Condition(), this.getCondition(), null, "condition", null, 0, 1, Sequence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getSequence_Order(), ecorePackage.getEInt(), "order", null, 0, 1, Sequence.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conditionEClass, Condition.class, "Condition", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCondition_Logical(), this.getLogicalExpression(), null, "logical", null, 0, 1, Condition.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(logicalExpressionEClass, LogicalExpression.class, "LogicalExpression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLogicalExpression_C1(), this.getConditionalUnary(), null, "c1", null, 0, 1, LogicalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getLogicalExpression_C2(), this.getConditionalUnary(), null, "c2", null, 0, -1, LogicalExpression.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(conditionalUnaryEClass, ConditionalUnary.class, "ConditionalUnary", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(conditionalTermEClass, ConditionalTerm.class, "ConditionalTerm", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getConditionalTerm_E1(), this.getExpression(), null, "e1", null, 0, 1, ConditionalTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getConditionalTerm_E2(), this.getExpression(), null, "e2", null, 0, 1, ConditionalTerm.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(expressionEClass, Expression.class, "Expression", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(addEClass, Add.class, "Add", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAdd_M1(), this.getMultiply(), null, "m1", null, 0, 1, Add.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getAdd_M2(), this.getMultiply(), null, "m2", null, 0, -1, Add.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(multiplyEClass, Multiply.class, "Multiply", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMultiply_U1(), this.getUnary(), null, "u1", null, 0, 1, Multiply.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMultiply_U2(), this.getUnary(), null, "u2", null, 0, -1, Multiply.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(unaryEClass, Unary.class, "Unary", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(termEClass, Term.class, "Term", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTerm_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, Term.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTerm_N(), ecorePackage.getEString(), "n", null, 0, 1, Term.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTerm_F(), this.getFunction(), null, "f", null, 0, 1, Term.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTerm_E(), this.getExpression(), null, "e", null, 0, 1, Term.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEAttribute(getTerm_S(), ecorePackage.getEString(), "s", null, 0, 1, Term.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(functionEClass, Function.class, "Function", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);

    initEClass(externalFunction1EClass, ExternalFunction1.class, "ExternalFunction1", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExternalFunction1_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, ExternalFunction1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExternalFunction1_E1(), ecorePackage.getEObject(), null, "e1", null, 0, 1, ExternalFunction1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExternalFunction1_E2(), ecorePackage.getEObject(), null, "e2", null, 0, -1, ExternalFunction1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getExternalFunction1_E0(), this.getExpression(), null, "e0", null, 0, 1, ExternalFunction1.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(externalFunction2EClass, ExternalFunction2.class, "ExternalFunction2", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getExternalFunction2_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, ExternalFunction2.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(trunkTimeArrayEClass, TrunkTimeArray.class, "TrunkTimeArray", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTrunkTimeArray_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, TrunkTimeArray.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTrunkTimeArray_T1(), this.getTrunkTimeArrayIndex(), null, "t1", null, 0, 1, TrunkTimeArray.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getTrunkTimeArray_T2(), this.getTrunkTimeArrayIndex(), null, "t2", null, 0, 1, TrunkTimeArray.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(trunkTimeArrayIndexEClass, TrunkTimeArrayIndex.class, "TrunkTimeArrayIndex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTrunkTimeArrayIndex_Ref(), this.getDeclaration(), null, "ref", null, 0, 1, TrunkTimeArrayIndex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(maxFunctionEClass, MaxFunction.class, "MaxFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMaxFunction_E1(), this.getExpression(), null, "e1", null, 0, 1, MaxFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMaxFunction_E2(), this.getExpression(), null, "e2", null, 0, -1, MaxFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(minFunctionEClass, MinFunction.class, "MinFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getMinFunction_E1(), this.getExpression(), null, "e1", null, 0, 1, MinFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getMinFunction_E2(), this.getExpression(), null, "e2", null, 0, -1, MinFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(modFunctionEClass, ModFunction.class, "ModFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getModFunction_E1(), this.getExpression(), null, "e1", null, 0, 1, ModFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getModFunction_E2(), this.getExpression(), null, "e2", null, 0, 1, ModFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(intFunctionEClass, IntFunction.class, "IntFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getIntFunction_E(), this.getExpression(), null, "e", null, 0, 1, IntFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(absFunctionEClass, AbsFunction.class, "AbsFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAbsFunction_E(), this.getExpression(), null, "e", null, 0, 1, AbsFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(roundFunctionEClass, RoundFunction.class, "RoundFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getRoundFunction_E(), this.getExpression(), null, "e", null, 0, 1, RoundFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(powFunctionEClass, PowFunction.class, "PowFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getPowFunction_E1(), this.getExpression(), null, "e1", null, 0, 1, PowFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getPowFunction_E2(), this.getExpression(), null, "e2", null, 0, 1, PowFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(logFunctionEClass, LogFunction.class, "LogFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getLogFunction_E(), this.getExpression(), null, "e", null, 0, 1, LogFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(sinFunctionEClass, SinFunction.class, "SinFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getSinFunction_E(), this.getExpression(), null, "e", null, 0, 1, SinFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cosFunctionEClass, CosFunction.class, "CosFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCosFunction_E(), this.getExpression(), null, "e", null, 0, 1, CosFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(tanFunctionEClass, TanFunction.class, "TanFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getTanFunction_E(), this.getExpression(), null, "e", null, 0, 1, TanFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(cotFunctionEClass, CotFunction.class, "CotFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getCotFunction_E(), this.getExpression(), null, "e", null, 0, 1, CotFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(asinFunctionEClass, AsinFunction.class, "AsinFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAsinFunction_E(), this.getExpression(), null, "e", null, 0, 1, AsinFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(acosFunctionEClass, AcosFunction.class, "AcosFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAcosFunction_E(), this.getExpression(), null, "e", null, 0, 1, AcosFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(atanFunctionEClass, AtanFunction.class, "AtanFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAtanFunction_E(), this.getExpression(), null, "e", null, 0, 1, AtanFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(acotFunctionEClass, AcotFunction.class, "AcotFunction", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getAcotFunction_E(), this.getExpression(), null, "e", null, 0, 1, AcotFunction.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(varModelEClass, VarModel.class, "VarModel", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVarModel_Ref1(), this.getDeclaration(), null, "ref1", null, 0, 1, VarModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVarModel_Ref2(), this.getModel(), null, "ref2", null, 0, 1, VarModel.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(varModelStepEClass, VarModelStep.class, "VarModelStep", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVarModelStep_Ref1(), this.getDeclaration(), null, "ref1", null, 0, 1, VarModelStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVarModelStep_Ref2(), this.getModel(), null, "ref2", null, 0, 1, VarModelStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVarModelStep_E(), this.getExpression(), null, "e", null, 0, 1, VarModelStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(varModelIndexEClass, VarModelIndex.class, "VarModelIndex", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVarModelIndex_Ref1(), this.getDeclaration(), null, "ref1", null, 0, 1, VarModelIndex.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(varModelIndexStepEClass, VarModelIndexStep.class, "VarModelIndexStep", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEReference(getVarModelIndexStep_Ref1(), this.getDeclaration(), null, "ref1", null, 0, 1, VarModelIndexStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
    initEReference(getVarModelIndexStep_E(), this.getExpression(), null, "e", null, 0, 1, VarModelIndexStep.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(identEClass, Ident.class, "Ident", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIdent_Name(), ecorePackage.getEString(), "name", null, 0, 1, Ident.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    initEClass(includeFileEClass, IncludeFile.class, "IncludeFile", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS);
    initEAttribute(getIncludeFile_File(), ecorePackage.getEString(), "file", null, 0, 1, IncludeFile.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);

    // Create resource
    createResource(eNS_URI);
  }

} //WreslEditorPackageImpl
