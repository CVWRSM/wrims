/**
 */
package gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl;

import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseIfTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.ElseTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IfTerm;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.LogicalExpression;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEditorPackage;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>If Term</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IfTermImpl#getElseifterm <em>Elseifterm</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IfTermImpl#getElseterm <em>Elseterm</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IfTermImpl#getLogical <em>Logical</em>}</li>
 *   <li>{@link gov.ca.dwr.wresl.xtext.editor.wreslEditor.impl.IfTermImpl#getPattern <em>Pattern</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class IfTermImpl extends IfIncItemsImpl implements IfTerm
{
  /**
   * The cached value of the '{@link #getElseifterm() <em>Elseifterm</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElseifterm()
   * @generated
   * @ordered
   */
  protected ElseIfTerm elseifterm;

  /**
   * The cached value of the '{@link #getElseterm() <em>Elseterm</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getElseterm()
   * @generated
   * @ordered
   */
  protected ElseTerm elseterm;

  /**
   * The cached value of the '{@link #getLogical() <em>Logical</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getLogical()
   * @generated
   * @ordered
   */
  protected LogicalExpression logical;

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
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected IfTermImpl()
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
    return WreslEditorPackage.Literals.IF_TERM;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElseIfTerm getElseifterm()
  {
    return elseifterm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetElseifterm(ElseIfTerm newElseifterm, NotificationChain msgs)
  {
    ElseIfTerm oldElseifterm = elseifterm;
    elseifterm = newElseifterm;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__ELSEIFTERM, oldElseifterm, newElseifterm);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setElseifterm(ElseIfTerm newElseifterm)
  {
    if (newElseifterm != elseifterm)
    {
      NotificationChain msgs = null;
      if (elseifterm != null)
        msgs = ((InternalEObject)elseifterm).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__ELSEIFTERM, null, msgs);
      if (newElseifterm != null)
        msgs = ((InternalEObject)newElseifterm).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__ELSEIFTERM, null, msgs);
      msgs = basicSetElseifterm(newElseifterm, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__ELSEIFTERM, newElseifterm, newElseifterm));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ElseTerm getElseterm()
  {
    return elseterm;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetElseterm(ElseTerm newElseterm, NotificationChain msgs)
  {
    ElseTerm oldElseterm = elseterm;
    elseterm = newElseterm;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__ELSETERM, oldElseterm, newElseterm);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setElseterm(ElseTerm newElseterm)
  {
    if (newElseterm != elseterm)
    {
      NotificationChain msgs = null;
      if (elseterm != null)
        msgs = ((InternalEObject)elseterm).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__ELSETERM, null, msgs);
      if (newElseterm != null)
        msgs = ((InternalEObject)newElseterm).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__ELSETERM, null, msgs);
      msgs = basicSetElseterm(newElseterm, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__ELSETERM, newElseterm, newElseterm));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public LogicalExpression getLogical()
  {
    return logical;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetLogical(LogicalExpression newLogical, NotificationChain msgs)
  {
    LogicalExpression oldLogical = logical;
    logical = newLogical;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__LOGICAL, oldLogical, newLogical);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLogical(LogicalExpression newLogical)
  {
    if (newLogical != logical)
    {
      NotificationChain msgs = null;
      if (logical != null)
        msgs = ((InternalEObject)logical).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__LOGICAL, null, msgs);
      if (newLogical != null)
        msgs = ((InternalEObject)newLogical).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - WreslEditorPackage.IF_TERM__LOGICAL, null, msgs);
      msgs = basicSetLogical(newLogical, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, WreslEditorPackage.IF_TERM__LOGICAL, newLogical, newLogical));
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
      pattern = new EObjectContainmentEList<Pattern>(Pattern.class, this, WreslEditorPackage.IF_TERM__PATTERN);
    }
    return pattern;
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
      case WreslEditorPackage.IF_TERM__ELSEIFTERM:
        return basicSetElseifterm(null, msgs);
      case WreslEditorPackage.IF_TERM__ELSETERM:
        return basicSetElseterm(null, msgs);
      case WreslEditorPackage.IF_TERM__LOGICAL:
        return basicSetLogical(null, msgs);
      case WreslEditorPackage.IF_TERM__PATTERN:
        return ((InternalEList<?>)getPattern()).basicRemove(otherEnd, msgs);
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
      case WreslEditorPackage.IF_TERM__ELSEIFTERM:
        return getElseifterm();
      case WreslEditorPackage.IF_TERM__ELSETERM:
        return getElseterm();
      case WreslEditorPackage.IF_TERM__LOGICAL:
        return getLogical();
      case WreslEditorPackage.IF_TERM__PATTERN:
        return getPattern();
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
      case WreslEditorPackage.IF_TERM__ELSEIFTERM:
        setElseifterm((ElseIfTerm)newValue);
        return;
      case WreslEditorPackage.IF_TERM__ELSETERM:
        setElseterm((ElseTerm)newValue);
        return;
      case WreslEditorPackage.IF_TERM__LOGICAL:
        setLogical((LogicalExpression)newValue);
        return;
      case WreslEditorPackage.IF_TERM__PATTERN:
        getPattern().clear();
        getPattern().addAll((Collection<? extends Pattern>)newValue);
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
      case WreslEditorPackage.IF_TERM__ELSEIFTERM:
        setElseifterm((ElseIfTerm)null);
        return;
      case WreslEditorPackage.IF_TERM__ELSETERM:
        setElseterm((ElseTerm)null);
        return;
      case WreslEditorPackage.IF_TERM__LOGICAL:
        setLogical((LogicalExpression)null);
        return;
      case WreslEditorPackage.IF_TERM__PATTERN:
        getPattern().clear();
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
      case WreslEditorPackage.IF_TERM__ELSEIFTERM:
        return elseifterm != null;
      case WreslEditorPackage.IF_TERM__ELSETERM:
        return elseterm != null;
      case WreslEditorPackage.IF_TERM__LOGICAL:
        return logical != null;
      case WreslEditorPackage.IF_TERM__PATTERN:
        return pattern != null && !pattern.isEmpty();
    }
    return super.eIsSet(featureID);
  }

} //IfTermImpl
