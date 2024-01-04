package gov.ca.dwr.wresl.xtext.editor.tests;

import gov.ca.dwr.wresl.xtext.editor.WreslEditorInjectorProvider;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Alias;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Goal;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.GoalSimple;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.IncludeFile;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Pattern;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.Sequence;
import gov.ca.dwr.wresl.xtext.editor.wreslEditor.WreslEvaluator;
import junit.framework.Assert;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.XtextRunner;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.google.inject.Inject;

@SuppressWarnings("all")
@RunWith(XtextRunner.class)
@InjectWith(WreslEditorInjectorProvider.class)
public class ParserTest {

	@Inject
	private ParseHelper<WreslEvaluator> parser;
	@Test
	public void testParsing() throws Exception {
		{

			WreslEvaluator evaluator = this.parser
					.parse("sequence XYZ { model ABC }\n"
							+ "model ABC{ include 'abc' }");
			Sequence sequence = evaluator.getSequence().get(0);
			String sequenceName = sequence.getName();
			Assert.assertEquals("XYZ", sequenceName);
			Assert.assertEquals("ABC", sequence.getModel().getName());
		}
	}

	@Test
	public void testInclude() throws Exception {

		WreslEvaluator evaluator = this.parser
				.parse("include '../test.xyz'");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertNotNull(pattern);
		EObject eObject = pattern.get(0);
		Assert.assertTrue(eObject instanceof IncludeFile);
		IncludeFile ifile=(IncludeFile) eObject;
		Assert.assertEquals("../test.xyz",ifile.getFile());
	}
	@Test
	public void testAlias() throws Exception {

		WreslEvaluator evaluator = this.parser
				.parse("define WTS_D815_Targ {alias D815_Targ kind 'WTS-Transfer-Target' units 'taf'}");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertNotNull(pattern);
		Alias p = (Alias) pattern.get(0);
		Assert.assertEquals("D815_Targ", p.getExpression());
		Assert.assertEquals("WTS-Transfer-Target",p.getKind());
		Assert.assertEquals("taf", p.getUnits());
	}
	@Test
	public void testGoalSimple() throws Exception {

		WreslEvaluator evaluator = this.parser
				.parse("goal[local] setWTS_SWPAlloc {WTS_SWPAlloc = perdel_swp_mwd1}");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertNotNull(pattern);
		Pattern p = (Pattern) pattern.get(0);
		Assert.assertTrue(p instanceof Goal);
		Goal g = (Goal) p;
		Assert.assertEquals("setWTS_SWPAlloc",g.getName());
		Assert.assertNotNull(g.getDefinition());
		EObject gobj = g.getDefinition();
		Assert.assertTrue(gobj instanceof GoalSimple);
		GoalSimple gs = (GoalSimple) gobj;
		Assert.assertEquals("WTS_SWPAlloc", gs.getConstraint().getLhs());
		Assert.assertEquals("=", gs.getConstraint().getOperator());
		Assert.assertEquals("perdel_swp_mwd1", gs.getConstraint().getRhs());
	}

	
	@Test
	public void testAliasGoalCombo() throws Exception{
		WreslEvaluator evaluator = this.parser.parse("define[local] cvpdeadstor {alias S1_1 + S4_1 + S8_1 units 'taf'}" +
				"goal[local] setWTS_SWPAlloc {WTS_SWPAlloc = perdel_swp_mwd1}");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertEquals(2, pattern.size());
		
	}
	
	@Test
	public void testDefineSvarValue() throws Exception{
		WreslEvaluator evaluator = this.parser.parse("    define CVP_Targ {value 0.}");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertEquals(1, pattern.size());
		Pattern p = (Pattern) pattern.get(0);
	}
	
	@Test
	public void testGoalWithCase() throws Exception{
		WreslEvaluator evaluator = this.parser.parse("define sacStorTrigger {\n" +
				"case Oct {\n" +
				"    condition   month == OCT\n" +
				"    value       S4(prevSEP) + S44(prevSEP) + S4_mod_dv(prevSep) }\n" +
				"  case other  {\n" +
				"    condition   always\n" +
				"    value       S4(-1) + S44(-1) + S4mod }\n" +
				"}");
		EList<Pattern> pattern = evaluator.getPattern();
		Assert.assertNotNull(pattern);
	}
}