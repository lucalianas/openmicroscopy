package ome.model;

import java.util.*;




/**
 * Image generated by hbm2java
 */
public class
Image 
implements java.io.Serializable ,
ome.api.OMEModel,
ome.util.Filterable {

    // Fields    

     private Integer imageId;
     private Date inserted;
     private String name;
     private String description;
     private Date created;
     private String imageGuid;
     private ImagePixel imagePixel;
     private Experimenter experimenter;
     private Set thumbnails;
     private Set renderingSettings;
     private Set logicalChannels;
     private Set imagePixels;
     private Set classifications;
     private Set features;
     private Set moduleExecutions;
     private Set imageAnnotations;
     private ImageDimension imageDimension;
     private Set imageInfos;
     private Set channelComponents;
     private Group group;
     private Set datasets;


    // Constructors

    /** default constructor */
    public Image() {
    }
    
    /** constructor with id */
    public Image(Integer imageId) {
        this.imageId = imageId;
    }
   
    
    

    // Property accessors

    /**
     * 
     */
    public Integer getImageId() {
        return this.imageId;
    }
    
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    /**
     * 
     */
    public Date getInserted() {
        return this.inserted;
    }
    
    public void setInserted(Date inserted) {
        this.inserted = inserted;
    }

    /**
     * 
     */
    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     */
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     */
    public Date getCreated() {
        return this.created;
    }
    
    public void setCreated(Date created) {
        this.created = created;
    }

    /**
     * 
     */
    public String getImageGuid() {
        return this.imageGuid;
    }
    
    public void setImageGuid(String imageGuid) {
        this.imageGuid = imageGuid;
    }

    /**
     * 
     */
    public ImagePixel getImagePixel() {
        return this.imagePixel;
    }
    
    public void setImagePixel(ImagePixel imagePixel) {
        this.imagePixel = imagePixel;
    }

    /**
     * 
     */
    public Experimenter getExperimenter() {
        return this.experimenter;
    }
    
    public void setExperimenter(Experimenter experimenter) {
        this.experimenter = experimenter;
    }

    /**
     * 
     */
    public Set getThumbnails() {
        return this.thumbnails;
    }
    
    public void setThumbnails(Set thumbnails) {
        this.thumbnails = thumbnails;
    }

    /**
     * 
     */
    public Set getRenderingSettings() {
        return this.renderingSettings;
    }
    
    public void setRenderingSettings(Set renderingSettings) {
        this.renderingSettings = renderingSettings;
    }

    /**
     * 
     */
    public Set getLogicalChannels() {
        return this.logicalChannels;
    }
    
    public void setLogicalChannels(Set logicalChannels) {
        this.logicalChannels = logicalChannels;
    }

    /**
     * 
     */
    public Set getImagePixels() {
        return this.imagePixels;
    }
    
    public void setImagePixels(Set imagePixels) {
        this.imagePixels = imagePixels;
    }

    /**
     * 
     */
    public Set getClassifications() {
        return this.classifications;
    }
    
    public void setClassifications(Set classifications) {
        this.classifications = classifications;
    }

    /**
     * 
     */
    public Set getFeatures() {
        return this.features;
    }
    
    public void setFeatures(Set features) {
        this.features = features;
    }

    /**
     * 
     */
    public Set getModuleExecutions() {
        return this.moduleExecutions;
    }
    
    public void setModuleExecutions(Set moduleExecutions) {
        this.moduleExecutions = moduleExecutions;
    }

    /**
     * 
     */
    public Set getImageAnnotations() {
        return this.imageAnnotations;
    }
    
    public void setImageAnnotations(Set imageAnnotations) {
        this.imageAnnotations = imageAnnotations;
    }

    /**
     * 
     */
    public ImageDimension getImageDimension() {
        return this.imageDimension;
    }
    
    public void setImageDimension(ImageDimension imageDimension) {
        this.imageDimension = imageDimension;
    }

    /**
     * 
     */
    public Set getImageInfos() {
        return this.imageInfos;
    }
    
    public void setImageInfos(Set imageInfos) {
        this.imageInfos = imageInfos;
    }

    /**
     * 
     */
    public Set getChannelComponents() {
        return this.channelComponents;
    }
    
    public void setChannelComponents(Set channelComponents) {
        this.channelComponents = channelComponents;
    }

    /**
     * 
     */
    public Group getGroup() {
        return this.group;
    }
    
    public void setGroup(Group group) {
        this.group = group;
    }

    /**
     * 
     */
    public Set getDatasets() {
        return this.datasets;
    }
    
    public void setDatasets(Set datasets) {
        this.datasets = datasets;
    }






  public boolean acceptFilter(ome.util.Filter filter){


	  // Visiting: ImageId ------------------------------------------
	  Integer _ImageId = null;
	  try {
	     _ImageId = getImageId();
	  } catch (Exception e) {
		 setImageId(null);
	  }

	  setImageId((Integer) filter.filter(IMAGEID,_ImageId)); 

	  // Visiting: Inserted ------------------------------------------
	  Date _Inserted = null;
	  try {
	     _Inserted = getInserted();
	  } catch (Exception e) {
		 setInserted(null);
	  }

	  setInserted((Date) filter.filter(INSERTED,_Inserted)); 

	  // Visiting: Name ------------------------------------------
	  String _Name = null;
	  try {
	     _Name = getName();
	  } catch (Exception e) {
		 setName(null);
	  }

	  setName((String) filter.filter(NAME,_Name)); 

	  // Visiting: Description ------------------------------------------
	  String _Description = null;
	  try {
	     _Description = getDescription();
	  } catch (Exception e) {
		 setDescription(null);
	  }

	  setDescription((String) filter.filter(DESCRIPTION,_Description)); 

	  // Visiting: Created ------------------------------------------
	  Date _Created = null;
	  try {
	     _Created = getCreated();
	  } catch (Exception e) {
		 setCreated(null);
	  }

	  setCreated((Date) filter.filter(CREATED,_Created)); 

	  // Visiting: ImageGuid ------------------------------------------
	  String _ImageGuid = null;
	  try {
	     _ImageGuid = getImageGuid();
	  } catch (Exception e) {
		 setImageGuid(null);
	  }

	  setImageGuid((String) filter.filter(IMAGEGUID,_ImageGuid)); 

	  // Visiting: ImagePixel ------------------------------------------
	  ImagePixel _ImagePixel = null;
	  try {
	     _ImagePixel = getImagePixel();
	  } catch (Exception e) {
		 setImagePixel(null);
	  }

	  setImagePixel((ImagePixel) filter.filter(IMAGEPIXEL,_ImagePixel)); 

	  // Visiting: Experimenter ------------------------------------------
	  Experimenter _Experimenter = null;
	  try {
	     _Experimenter = getExperimenter();
	  } catch (Exception e) {
		 setExperimenter(null);
	  }

	  setExperimenter((Experimenter) filter.filter(EXPERIMENTER,_Experimenter)); 

	  // Visiting: Thumbnails ------------------------------------------
	  Set _Thumbnails = null;
	  try {
	     _Thumbnails = getThumbnails();
	  } catch (Exception e) {
		 setThumbnails(null);
	  }

	  setThumbnails((Set) filter.filter(THUMBNAILS,_Thumbnails)); 

	  // Visiting: RenderingSettings ------------------------------------------
	  Set _RenderingSettings = null;
	  try {
	     _RenderingSettings = getRenderingSettings();
	  } catch (Exception e) {
		 setRenderingSettings(null);
	  }

	  setRenderingSettings((Set) filter.filter(RENDERINGSETTINGS,_RenderingSettings)); 

	  // Visiting: LogicalChannels ------------------------------------------
	  Set _LogicalChannels = null;
	  try {
	     _LogicalChannels = getLogicalChannels();
	  } catch (Exception e) {
		 setLogicalChannels(null);
	  }

	  setLogicalChannels((Set) filter.filter(LOGICALCHANNELS,_LogicalChannels)); 

	  // Visiting: ImagePixels ------------------------------------------
	  Set _ImagePixels = null;
	  try {
	     _ImagePixels = getImagePixels();
	  } catch (Exception e) {
		 setImagePixels(null);
	  }

	  setImagePixels((Set) filter.filter(IMAGEPIXELS,_ImagePixels)); 

	  // Visiting: Classifications ------------------------------------------
	  Set _Classifications = null;
	  try {
	     _Classifications = getClassifications();
	  } catch (Exception e) {
		 setClassifications(null);
	  }

	  setClassifications((Set) filter.filter(CLASSIFICATIONS,_Classifications)); 

	  // Visiting: Features ------------------------------------------
	  Set _Features = null;
	  try {
	     _Features = getFeatures();
	  } catch (Exception e) {
		 setFeatures(null);
	  }

	  setFeatures((Set) filter.filter(FEATURES,_Features)); 

	  // Visiting: ModuleExecutions ------------------------------------------
	  Set _ModuleExecutions = null;
	  try {
	     _ModuleExecutions = getModuleExecutions();
	  } catch (Exception e) {
		 setModuleExecutions(null);
	  }

	  setModuleExecutions((Set) filter.filter(MODULEEXECUTIONS,_ModuleExecutions)); 

	  // Visiting: ImageAnnotations ------------------------------------------
	  Set _ImageAnnotations = null;
	  try {
	     _ImageAnnotations = getImageAnnotations();
	  } catch (Exception e) {
		 setImageAnnotations(null);
	  }

	  setImageAnnotations((Set) filter.filter(IMAGEANNOTATIONS,_ImageAnnotations)); 

	  // Visiting: ImageDimension ------------------------------------------
	  ImageDimension _ImageDimension = null;
	  try {
	     _ImageDimension = getImageDimension();
	  } catch (Exception e) {
		 setImageDimension(null);
	  }

	  setImageDimension((ImageDimension) filter.filter(IMAGEDIMENSION,_ImageDimension)); 

	  // Visiting: ImageInfos ------------------------------------------
	  Set _ImageInfos = null;
	  try {
	     _ImageInfos = getImageInfos();
	  } catch (Exception e) {
		 setImageInfos(null);
	  }

	  setImageInfos((Set) filter.filter(IMAGEINFOS,_ImageInfos)); 

	  // Visiting: ChannelComponents ------------------------------------------
	  Set _ChannelComponents = null;
	  try {
	     _ChannelComponents = getChannelComponents();
	  } catch (Exception e) {
		 setChannelComponents(null);
	  }

	  setChannelComponents((Set) filter.filter(CHANNELCOMPONENTS,_ChannelComponents)); 

	  // Visiting: Group ------------------------------------------
	  Group _Group = null;
	  try {
	     _Group = getGroup();
	  } catch (Exception e) {
		 setGroup(null);
	  }

	  setGroup((Group) filter.filter(GROUP,_Group)); 

	  // Visiting: Datasets ------------------------------------------
	  Set _Datasets = null;
	  try {
	     _Datasets = getDatasets();
	  } catch (Exception e) {
		 setDatasets(null);
	  }

	  setDatasets((Set) filter.filter(DATASETS,_Datasets)); 
   	 return true;
  }
  
  public String toString(){
	return "Image"+(imageId==null ? ":Hash_"+this.hashCode() : ":Id_"+imageId);
  }
  
  // FIELD-FIELDS
  
	public final static String IMAGEID = "Image_ImageId";
	public final static String INSERTED = "Image_Inserted";
	public final static String NAME = "Image_Name";
	public final static String DESCRIPTION = "Image_Description";
	public final static String CREATED = "Image_Created";
	public final static String IMAGEGUID = "Image_ImageGuid";
	public final static String IMAGEPIXEL = "Image_ImagePixel";
	public final static String EXPERIMENTER = "Image_Experimenter";
	public final static String THUMBNAILS = "Image_Thumbnails";
	public final static String RENDERINGSETTINGS = "Image_RenderingSettings";
	public final static String LOGICALCHANNELS = "Image_LogicalChannels";
	public final static String IMAGEPIXELS = "Image_ImagePixels";
	public final static String CLASSIFICATIONS = "Image_Classifications";
	public final static String FEATURES = "Image_Features";
	public final static String MODULEEXECUTIONS = "Image_ModuleExecutions";
	public final static String IMAGEANNOTATIONS = "Image_ImageAnnotations";
	public final static String IMAGEDIMENSION = "Image_ImageDimension";
	public final static String IMAGEINFOS = "Image_ImageInfos";
	public final static String CHANNELCOMPONENTS = "Image_ChannelComponents";
	public final static String GROUP = "Image_Group";
	public final static String DATASETS = "Image_Datasets";
 	public final static Set FIELDS = new HashSet();
	static {
	   FIELDS.add(IMAGEID);
	   FIELDS.add(INSERTED);
	   FIELDS.add(NAME);
	   FIELDS.add(DESCRIPTION);
	   FIELDS.add(CREATED);
	   FIELDS.add(IMAGEGUID);
	   FIELDS.add(IMAGEPIXEL);
	   FIELDS.add(EXPERIMENTER);
	   FIELDS.add(THUMBNAILS);
	   FIELDS.add(RENDERINGSETTINGS);
	   FIELDS.add(LOGICALCHANNELS);
	   FIELDS.add(IMAGEPIXELS);
	   FIELDS.add(CLASSIFICATIONS);
	   FIELDS.add(FEATURES);
	   FIELDS.add(MODULEEXECUTIONS);
	   FIELDS.add(IMAGEANNOTATIONS);
	   FIELDS.add(IMAGEDIMENSION);
	   FIELDS.add(IMAGEINFOS);
	   FIELDS.add(CHANNELCOMPONENTS);
	   FIELDS.add(GROUP);
	   FIELDS.add(DATASETS);
 	}


}
