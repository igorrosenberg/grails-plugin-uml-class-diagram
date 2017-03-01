package umlclassdiagram

class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?"{
            constraints {
                // apply constraints here
            }
        }

        "/"(view:"/index")
        "/2"(view:"/2")
        "/3"(view:"/uml/index")
        "500"(view:'/error')
        "404"(view:'/notFound')
    }
}
