import { createStandaloneToast } from '@chakra-ui/react'

const { toast } = createStandaloneToast()

const notification = (title, description, status) => {
    toast({
        title,
        description,
        status,
        isClosable: true,
        duration: 5000  // 5000 millisecond which equal 5 second
    })
}

export const successNotification = (title, description) => {
    notification(
        title,
        description,
        "success"
    )
}

export const errorNotification = (title, description) => {
    notification(
        title,
        description,
        "error"
    )
}