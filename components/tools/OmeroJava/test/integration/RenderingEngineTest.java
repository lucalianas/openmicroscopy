/*
 * $Id$
 *
 *   Copyright 2006-2010 University of Dundee. All rights reserved.
 *   Use is subject to license terms supplied in LICENSE.txt
 */
package integration;


//Java imports
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//Third-party libraries
import org.testng.annotations.Test;

//Application-internal dependencies
import omero.api.IPixelsPrx;
import omero.api.RenderingEnginePrx;
import omero.model.ChannelBinding;
import omero.model.Family;
import omero.model.IObject;
import omero.model.Image;
import omero.model.Pixels;
import omero.model.QuantumDef;
import omero.model.RenderingDef;
import omero.model.RenderingModel;
import omero.romio.PlaneDef;
import omero.romio.RGBBuffer;
import omero.romio.RegionDef;

/** 
 * Collection of tests for the <code>RenderingEngine</code>.
 *
 * @author Jean-Marie Burel &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:j.burel@dundee.ac.uk">j.burel@dundee.ac.uk</a>
 * @author Donald MacDonald &nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="mailto:donald@lifesci.dundee.ac.uk">donald@lifesci.dundee.ac.uk</a>
 * @version 3.0
 * <small>
 * (<b>Internal version:</b> $Revision: $Date: $)
 * </small>
 * @since 3.0-Beta4
 */
@Test(groups = { "client", "integration", "blitz" })
public class RenderingEngineTest
	extends AbstractTest
{
	
	/**
	 * Checks the value of the bytes are the same.
	 * 
	 * @param region The region.
	 * @param plane  The plane.
	 * @param w The width of a step.
	 * @param rWidth The width of the region.
	 */
	private void checkBuffer(byte[] region, byte[] plane, int w, int rWidth)
	{
		int j;
		int k = 0;
		for (int i = 0; i < region.length; i++) { 
			j = w*k+i;
			byte b = region[i];
			assertEquals(b, plane[j]);
			if (i%rWidth == rWidth-1)
				k++;
		}
	}
	
	/**
	 * Tests the creation of the rendering engine for a given pixels set w/o
	 * looking up for rendering settings.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testCreateRenderingEngineNoSettings()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
        Pixels pixels = image.getPrimaryPixels();
		RenderingEnginePrx svc = factory.createRenderingEngine();
		try {
			svc.lookupPixels(pixels.getId().getValue());
			svc.load();
			fail("We should not have been able to load it.");
		} catch (Exception e) {
			
		}
	}
	
	/**
	 * Tests the creation of the rendering engine for a given pixels set when
	 * looking up for rendering settings.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testCreateRenderingEngine()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		RenderingEnginePrx svc = factory.createRenderingEngine();
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		svc.lookupPixels(id);
		svc.resetDefaults();
		svc.lookupRenderingDef(id);
		svc.load();
		svc.close();
	}
	
	/**
	 * Tests the retrieval of the rendering settings data using the rendering 
	 * engine.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testRenderingEngineGetters()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		RenderingEnginePrx re = factory.createRenderingEngine();
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		re.lookupPixels(id);
		re.resetDefaults();
		re.lookupRenderingDef(id);
		re.load();
		//retrieve the rendering def
		RenderingDef def = factory.getPixelsService().retrieveRndSettings(id);
		assertTrue(def.getDefaultZ().getValue() == re.getDefaultZ());
		assertTrue(def.getDefaultT().getValue() == re.getDefaultT());
		assertTrue(def.getModel().getValue().getValue().equals( 
				re.getModel().getValue().getValue()));
		QuantumDef q1 = def.getQuantization();
		QuantumDef q2 = re.getQuantumDef();
		assertNotNull(q1);
		assertNotNull(q2);
		assertTrue(q1.getBitResolution().getValue() == 
			q2.getBitResolution().getValue());
		assertTrue(q1.getCdStart().getValue() == 
			q2.getCdStart().getValue());
		assertTrue(q1.getCdEnd().getValue() == 
			q2.getCdEnd().getValue());
		List<ChannelBinding> channels1 = def.copyWaveRendering();
		assertNotNull(channels1);
		Iterator<ChannelBinding> i = channels1.iterator();
		ChannelBinding c1;
		int index = 0;
		int[] rgba;
		while (i.hasNext()) {
			c1 = i.next();
			rgba = re.getRGBA(index);
			assertTrue(c1.getRed().getValue() == rgba[0]);
			assertTrue(c1.getGreen().getValue() == rgba[1]);
			assertTrue(c1.getBlue().getValue() == rgba[2]);
			assertTrue(c1.getAlpha().getValue() == rgba[3]);
			assertTrue(c1.getCoefficient().getValue() 
					== re.getChannelCurveCoefficient(index));
			assertTrue(c1.getFamily().getValue().getValue().equals(
					re.getChannelFamily(index).getValue().getValue()));
			assertTrue(c1.getInputStart().getValue() == 
				re.getChannelWindowStart(index));
			assertTrue(c1.getInputEnd().getValue() == 
				re.getChannelWindowEnd(index));
			Boolean b1 = Boolean.valueOf(c1.getActive().getValue());
			Boolean b2 = Boolean.valueOf(re.isActive(index));
			assertTrue(b1.equals(b2));
			b1 = Boolean.valueOf(c1.getNoiseReduction().getValue());
			b2 = Boolean.valueOf(re.getChannelNoiseReduction(index));
			assertTrue(b1.equals(b2));
			index++;
		}
		re.close();
	}
	
	/**
	 * Tests to modify the rendering settings using the rendering engine.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testRenderingEngineSetters()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		RenderingEnginePrx re = factory.createRenderingEngine();
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		re.lookupPixels(id);
		re.resetDefaults();
		re.lookupRenderingDef(id);
		re.load();
		RenderingDef def = factory.getPixelsService().retrieveRndSettings(id);
		int v = def.getDefaultT().getValue()+1;
		re.setDefaultT(v);
		assertTrue(re.getDefaultT() == v);
		v = def.getDefaultZ().getValue()+1;
		re.setDefaultZ(v);
		assertTrue(re.getDefaultZ() == v);
		
		//tested in PixelsService
		IPixelsPrx svc = factory.getPixelsService();
    	List<IObject> families = svc.getAllEnumerations(Family.class.getName());
    	List<IObject> models = svc.getAllEnumerations(
    			RenderingModel.class.getName());
    	RenderingModel model = def.getModel();
    	Iterator<IObject> i;
    	RenderingModel m;
    	i = models.iterator();
    	while (i.hasNext()) {
			m = (RenderingModel) i.next();
			if (m.getId().getValue() != model.getId().getValue()) {
				model = m;	
				break;
			}
		}
    	re.setModel(model);
    	assertTrue(re.getModel().getId().getValue() == model.getId().getValue());
    	QuantumDef qdef = def.getQuantization();
    	int start = qdef.getCdStart().getValue()+10;
    	int end = qdef.getCdEnd().getValue()-10;
    	re.setCodomainInterval(start, end);
    	assertTrue(re.getQuantumDef().getCdStart().getValue() == start);
    	assertTrue(re.getQuantumDef().getCdEnd().getValue() == end);
    	List<ChannelBinding> channels1 = def.copyWaveRendering();
		assertNotNull(channels1);
		Iterator<ChannelBinding> j = channels1.iterator();
		ChannelBinding c1;
		int index = 0;
		boolean b;
		double s, e;
		int[] RGBA = {255, 0, 10, 200};
		int[] rgba;
		double coefficient = 0.5;
		Family f = (Family) families.get(families.size()-1);
		while (j.hasNext()) {
			c1 = j.next();
			b = !c1.getActive().getValue();
			re.setActive(index, b);
			assertTrue(Boolean.valueOf(b).equals(
					Boolean.valueOf(re.isActive(index))));
			s = c1.getInputStart().getValue()+1;
			e = c1.getInputEnd().getValue()+1;
			re.setChannelWindow(index, s, e);
			assertTrue(re.getChannelWindowStart(index) == s);
			assertTrue(re.getChannelWindowEnd(index) == e);
			b = !c1.getNoiseReduction().getValue();
			re.setRGBA(index, RGBA[0], RGBA[1], RGBA[2], RGBA[3]);
			rgba = re.getRGBA(index);
			for (int k = 0; k < rgba.length; k++) {
				assertTrue(rgba[k] == RGBA[k]);
			}
			b = !c1.getNoiseReduction().getValue();
			re.setQuantizationMap(index, f, coefficient, b);
			assertTrue(Boolean.valueOf(
					re.getChannelNoiseReduction(index)).equals(
							Boolean.valueOf(b)));
			assertTrue(re.getChannelCurveCoefficient(index) == coefficient);
			assertTrue(re.getChannelFamily(index).getId().getValue() == 
				f.getId().getValue());
		}
		re.close();
	}
	
	/**
	 * Tests to reset the default settings but do not save them back to the 
	 * database using the <code>resetDefaultsNoSave</code> method.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test(enabled=false)
	public void testResetDefaultsNoSave()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		
		//create rendering settings and modify it
		List<Long> ids = new ArrayList<Long>();
		ids.add(image.getId().getValue());
		factory.getRenderingSettingsService().resetDefaultsInSet(
				Image.class.getName(), ids);
		RenderingDef def = factory.getPixelsService().retrieveRndSettings(id);
		int t = def.getDefaultT().getValue();
		int v = t+1;
		def.setDefaultT(omero.rtypes.rint(v));
		//update
		def = (RenderingDef) iUpdate.saveAndReturnObject(def);
		
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!re.lookupRenderingDef(id)) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		assertTrue(re.getDefaultT() == def.getDefaultT().getValue());
		re.resetDefaultsNoSave();
		assertTrue(re.getDefaultT() == t);
		//reload from db
		def = factory.getPixelsService().retrieveRndSettings(id);
		assertTrue(def.getDefaultT().getValue() == v);
	}
	
	/**
	 * Tests to reset the default settings and save them back to the database
	 * using the <code>resetDefaults</code> method.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testResetDefaults()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		
		//create rendering settings and modify it
		List<Long> ids = new ArrayList<Long>();
		ids.add(image.getId().getValue());
		factory.getRenderingSettingsService().resetDefaultsInSet(
				Image.class.getName(), ids);
		RenderingDef def = factory.getPixelsService().retrieveRndSettings(id);
		int t = def.getDefaultT().getValue();
		int v = t+1;
		def.setDefaultT(omero.rtypes.rint(v));
		//update
		def = (RenderingDef) iUpdate.saveAndReturnObject(def);
		
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!re.lookupRenderingDef(id)) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		assertTrue(re.getDefaultT() == def.getDefaultT().getValue());
		re.resetDefaults();
		assertTrue(re.getDefaultT() == t);
	}
	
	/**
	 * Tests to modify the rendering settings using the rendering engine 
	 * and save the current settings using the <code>saveCurrentSettings</code>
	 * method.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testSaveCurrentSettings()
		throws Exception
	{
		Image image = mmFactory.createImage();
    	image = (Image) iUpdate.saveAndReturnObject(image);
		Pixels pixels = image.getPrimaryPixels();
		long id = pixels.getId().getValue();
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!(re.lookupRenderingDef(id))) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		int t = re.getDefaultT();
		int v = t+1;
		re.setDefaultT(v);
		re.saveCurrentSettings();
		assertTrue(re.getDefaultT() == v);
		RenderingDef def = factory.getPixelsService().retrieveRndSettings(id);
		assertTrue(def.getDefaultT().getValue() == v);
		re.close();
	}
	
	/**
	 * Tests to render a plane.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testRenderPlane()
		throws Exception
	{
		File f = File.createTempFile("testRenderPlane", "."+OME_FORMAT);
		XMLMockObjects xml = new XMLMockObjects();
		XMLWriter writer = new XMLWriter();
		writer.writeFile(f, xml.createImage(), true);
		List<Pixels> pixels = null;
		try {
			pixels = importFile(f, OME_FORMAT);
		} catch (Throwable e) {
			throw new Exception("cannot import image", e);
		}
		Pixels p = pixels.get(0);
		long id = p.getId().getValue();
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!(re.lookupRenderingDef(id))) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		PlaneDef pDef = new PlaneDef();
		pDef.t = re.getDefaultT();
		pDef.z = re.getDefaultZ();
		pDef.slice = omero.romio.XY.value;
		//delete the file.
		RGBBuffer buffer = re.render(pDef);
		assertNotNull(buffer);
		assertEquals(p.getSizeX().getValue(), buffer.sizeX1);
		assertEquals(p.getSizeY().getValue(), buffer.sizeX2);
		f.delete();
		re.close();
	}
	
	/**
	 * Tests to render a given region of plane.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test
	public void testRenderRegion()
		throws Exception
	{
		File f = File.createTempFile("testRenderRegion", "."+OME_FORMAT);
		XMLMockObjects xml = new XMLMockObjects();
		XMLWriter writer = new XMLWriter();
		writer.writeFile(f, xml.createImage(), true);
		List<Pixels> pixels = null;
		try {
			pixels = importFile(f, OME_FORMAT);
		} catch (Throwable e) {
			throw new Exception("cannot import image", e);
		}
		Pixels p = pixels.get(0);
		long id = p.getId().getValue();
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!(re.lookupRenderingDef(id))) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		int[] values = {1, 2, 3, 4, 5};
		int v;
		int sizeX = p.getSizeX().getValue();
		int sizeY = p.getSizeY().getValue();
		PlaneDef pDef;
		RGBBuffer bufferRegion, bufferPlane;
		byte[] region, plane;
		for (int i = 0; i < values.length; i++) {
			v = values[i];
			pDef = new PlaneDef();
			pDef.t = re.getDefaultT();
			pDef.z = re.getDefaultZ();
			pDef.slice = omero.romio.XY.value;
			RegionDef r = new RegionDef();
			r.x = 0;
			r.y = 0;
			r.width = sizeX/v;
			r.height = sizeY/v;
			pDef.region = r;
			bufferRegion = re.render(pDef);
			assertNotNull(bufferRegion);
			assertEquals(r.width, bufferRegion.sizeX1);
			assertEquals(r.height, bufferRegion.sizeX2);
			//now render a plane and compare the renderer value.
			pDef = new PlaneDef();
			pDef.t = re.getDefaultT();
			pDef.z = re.getDefaultZ();
			pDef.slice = omero.romio.XY.value;
			bufferPlane = re.render(pDef);
			assertNotNull(bufferPlane);
			//red band
			region = bufferRegion.bands[0];
			plane = bufferPlane.bands[0];
			assertNotNull(region);
			assertNotNull(plane);
			checkBuffer(region, plane, sizeX-r.width, r.width);
			//green band
			region = bufferRegion.bands[1];
			plane = bufferPlane.bands[1];
			assertNotNull(region);
			assertNotNull(plane);
			checkBuffer(region, plane, sizeX-r.width, r.width);
			//blue band
			region = bufferRegion.bands[2];
			plane = bufferPlane.bands[2];
			assertNotNull(region);
			assertNotNull(plane);
			checkBuffer(region, plane, sizeX-r.width, r.width);
		}	
		f.delete();
		re.close();
	}

	/**
	 * Tests to render a given region of plane.
	 * @throws Exception Thrown if an error occurred.
	 */
	@Test(enabled = false)
	public void testRenderStridePlane()
		throws Exception
	{
		File f = File.createTempFile("testRenderStridePlane", "."+OME_FORMAT);
		XMLMockObjects xml = new XMLMockObjects();
		XMLWriter writer = new XMLWriter();
		writer.writeFile(f, xml.createImage(), true);
		List<Pixels> pixels = null;
		try {
			pixels = importFile(f, OME_FORMAT);
		} catch (Throwable e) {
			throw new Exception("cannot import image", e);
		}
		Pixels p = pixels.get(0);
		long id = p.getId().getValue();
		RenderingEnginePrx re = factory.createRenderingEngine();
		re.lookupPixels(id);
		if (!(re.lookupRenderingDef(id))) {
			re.resetDefaults();
			re.lookupRenderingDef(id);
		}
		re.load();
		PlaneDef pDef = new PlaneDef();
		pDef.t = re.getDefaultT();
		pDef.z = re.getDefaultZ();
		pDef.slice = omero.romio.XY.value;
		pDef.stride = 1;
		//delete the file.
		int sizeX = p.getSizeX().getValue();
		int sizeY = p.getSizeX().getValue();
		RGBBuffer buffer = re.render(pDef);
		assertNotNull(buffer);
		assertEquals(sizeX/(pDef.stride+1), buffer.sizeX1);
		assertEquals(sizeY/(pDef.stride+1), buffer.sizeX2);
		f.delete();
		re.close();
	}
	
}
