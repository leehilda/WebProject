package org.generation.WebProject.controller;

import org.generation.WebProject.component.FileUploadUtil;
import org.generation.WebProject.controller.dto.ItemDto;
import org.generation.WebProject.repository.entity.Item;
import org.generation.WebProject.sevice.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

//This is to do rest API mapping
@RestController
@RequestMapping("/item")

//item controller is dependent on itemservice to perform the save, delete, all
public class ItemController {

    @Value("${image.folder}")
    private String imageFolder; //imageFolder variable the value = productimages

    final ItemService itemService;

    public ItemController( @Autowired ItemService itemService )
    {
        this.itemService = itemService; //constructor
    }

    //1)Create an API endpoint for GET Http request from the client

    @CrossOrigin //Cross origin resource sharing (CORS) for development stage. Will remove when deployment
    @GetMapping( "/all" )
    public Iterable<Item> getItems()
    {
        return itemService.all();
    }

    //2) Create an API endpoint for GET Http request from the client by ID
    @CrossOrigin
    @GetMapping( "/{id}" ) //curlybraces is path element.
    public Item findItemById( @PathVariable Integer id )
    {
        return itemService.findbyId( id );
    }

//    3) Create an API endpoint for DELETE Http request from the client by ID
    @CrossOrigin
    @DeleteMapping ( "/{id}" )
    public void delete( @PathVariable Integer id )
    {
        itemService.delete( id );
    }

    //4) Create an API endpoint for POST HTTP Request from client
    @CrossOrigin
    @PostMapping("/add")
    public void save(  @RequestParam(name="name", required = true) String name,
                       @RequestParam(name="description", required = true) String description,
                       @RequestParam(name="imageUrl", required = true) String imageUrl,
                       @RequestParam(name="style", required = true) String style,
                       @RequestParam(name="price", required = true) double price,
                       @RequestParam("imagefile") MultipartFile multipartFile) throws IOException {

        //Prepare the fileName by cleaning up the path for saving the image file.
        //Part 1 Upload the image file into the productImage folder in the server
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        FileUploadUtil.saveFile(imageFolder, fileName, multipartFile);

        // part 2 save all the records in to mySQL database
        String fullPath = imageFolder +  "/" + imageUrl; //this is to store the image path
        ItemDto itemDto = new ItemDto(name, description, fullPath, style, price);
        itemService.save(new Item(itemDto));
    }



}
