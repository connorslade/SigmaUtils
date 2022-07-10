// cargo-deps: image = "0.24.2"
// Loads all minecraft block images and writes a file of all the ones with no transparent pixels

use image::{self, GenericImageView};
use std::fs;

fn main() {
    let mut out = Vec::new();

    'img: for i in fs::read_dir("./block").unwrap().map(|x| x.unwrap()) {
        let name = i.file_name().to_str().unwrap().to_owned();
        if !name.ends_with(".png") || name.contains("destroy") {
            continue;
        }

        let image = image::open(i.path()).unwrap();
        let dimensions = image.dimensions();

        if dimensions.0 != 16 || dimensions.1 != 16 {
            continue 'img;
        }

        for y in 0..dimensions.1 {
            for x in 0..dimensions.0 {
                if image.get_pixel(x, y).0[3] == 0 {
                    continue 'img;
                }
            }
        }

        out.push(name.rsplitn(2, ".").nth(1).unwrap().to_owned());
    }

    fs::write("out.txt", out.join("\n")).unwrap();
}
