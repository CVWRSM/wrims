/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfIncItems;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Initial;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Sequence;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEvaluator;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Wresl Evaluator</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.WreslEvaluatorImpl#getPattern <em>Pattern</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.WreslEvaluatorImpl#getIfincitem <em>Ifincitem</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.WreslEvaluatorImpl#getInitial <em>Initial</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.WreslEvaluatorImpl#getSequence <em>Sequence</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.WreslEvaluatorImpl#getModel <em>Model</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class WreslEvaluatorImpl extends MinimalEObjectImpl.Container implements WreslEvaluator
{
  /**
   * The cached value of the '{@link #getPattern() <em>Pattern</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPattern()
   * @generated
   * @ordered
   */
  protected EList<Pattern> pattern;

  /**
   * The cached value of the '{@link #getIfincitem() <em>Ifincitem</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getIfincitem()
   * @generated
   * @ordered
   */
  protected EList<IfIncItems> ifincitem;

  /**
   * The cached value of the '{@link #getInitial() <em>Initial</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getInitial()
   * @generated
   * @ordered
   */
  protected Initial initial;

  /**
   * The cached value of the '{@link #getSequence() <em>Sequence</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSequence()
   * @generated
   * @ordered
   */
  protected EList<Sequence> sequence;

  /**
   * The cached value of the '{@link #getModel() <em>Model</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getModel()
   * @generated
   * @ordered
   */
  protected EList<EObject> model;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WreslEvaluatorImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return WreslEditorPackage.Literals.WRESL_EVALUATOR;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Pattern> getPattern()
  {
    if (pattern == null)
    {
      pattern = new EObjectContainmentEList<Pattern>(Pattern.class, this, WreslEditorPackage.WRESL_EVALUATOR__PATTERN);
    }
    return pattern;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<IfIncItems> getIfincitem()
  {
    if (ifincitem == null)
    {
      ifincitem = new EObjectContainmentEList<IfIncItems>(IfIncItems.class, this, WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM);
    }
    return ifincitem;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Initial getInitial()
  {
    return initial;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetInitial(Initial newInitial, NotificationChain msgs)
  {
    Initial oldInitial = initial;
    initial = newInitial;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.WRESL_EVALUATOR__INITIAL, oldInitial, newInitial);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setInitial(Initial newInitial)
  {
    if (newInitial != initial)
    {
      NotificationChain msgs = null;
      if (initial != null)
        msgs = ((InternalEObject)initial).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.WRESL_EVALUATOR__INITIAL, null, msgs);
      if (newInitial != null)
        msgs = ((InternalEObject)newInitial).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.WRESL_EVALUATOR__INITIAL, null, msgs);
      msgs = basicSetInitial(newInitial, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.WRESL_EVALUATOR__INITIAL, newInitial, newInitial));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Sequence> getSequence()
  {
    if (sequence == null)
    {
      sequence = new EObjectContainmentEList<Sequence>(Sequence.class, this, WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE);
    }
    return sequence;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<EObject> getModel()
  {
    if (model == null)
    {
      model = new EObjectContainmentEList<EObject>(EObject.class, this, WreslEditorPackage.WRESL_EVALUATOR__MODEL);
    }
    return model;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR__PATTERN:
        return ((InternalEList<?>)getPattern()).basicRemove(otherEnd, msgs);
      case WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM:
        return ((InternalEList<?>)getIfincitem()).basicRemove(otherEnd, msgs);
      case WreslEditorPackage.WRESL_EVALUATOR__INITIAL:
        return basicSetInitial(null, msgs);
      case WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE:
        return ((InternalEList<?>)getSequence()).basicRemove(otherEnd, msgs);
      case WreslEditorPackage.WRESL_EVALUATOR__MODEL:
        return ((InternalEList<?>)getModel()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR__PATTERN:
        return getPattern();
      case WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM:
        return getIfincitem();
      case WreslEditorPackage.WRESL_EVALUATOR__INITIAL:
        return getInitial();
      case WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE:
        return getSequence();
      case WreslEditorPackage.WRESL_EVALUATOR__MODEL:
        return getModel();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR__PATTERN:
        getPattern().clear();
        getPattern().addAll((Collection<? extends Pattern>)newValue);
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM:
        getIfincitem().clear();
        getIfincitem().addAll((Collection<? extends IfIncItems>)newValue);
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__INITIAL:
        setInitial((Initial)newValue);
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE:
        getSequence().clear();
        getSequence().addAll((Collection<? extends Sequence>)newValue);
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__MODEL:
        getModel().clear();
        getModel().addAll((Collection<? extends EObject>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR__PATTERN:
        getPattern().clear();
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM:
        getIfincitem().clear();
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__INITIAL:
        setInitial((Initial)null);
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE:
        getSequence().clear();
        return;
      case WreslEditorPackage.WRESL_EVALUATOR__MODEL:
        getModel().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case WreslEditorPackage.WRESL_EVALUATOR__PATTERN:
        return pattern != null && !pattern.isEmpty();
      case WreslEditorPackage.WRESL_EVALUATOR__IFINCITEM:
        return ifincitem != null && !ifincitem.isEmpty();
      case WreslEditorPackage.WRESL_EVALUATOR__INITIAL:
        return initial != null;
      case WreslEditorPackage.WRESL_EVALUATOR__SEQUENCE:
        return sequence != null && !sequence.isEmpty();
      case WreslEditorPackage.WRESL_EVALUATOR__MODEL:
        return model != null && !model.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //WreslEvaluatorImpl
